import 'package:flutter/material.dart';
import 'package:http/http.dart';
import 'package:xml/xml.dart' as xml;
import 'dart:async';

import 'package:xml/xml.dart';

const String ATOM_URL = "https://www.xkcd.com/atom.xml";

Future<List<Comic>> _getSortedListOfComics() async {
  var result = <Comic>[];
  var httpClient = new Client();
  var response = await httpClient.read(ATOM_URL);
  var document = xml.parse(response);
  var entries = document.findAllElements("entry");
  for (xml.XmlElement entry in entries) {
    final comic = new Comic();
    var tags = entry.children;
    tags.forEach((var element) {
      switch ((element as xml.XmlElement).name.local) {
        case "title":
          comic.title = element.text;
          break;
        case "summary":
          _parseSummary(element.firstChild, comic);
          break;
      }
    });
    result.add(comic);
  }
  return result;
}

_parseSummary(xml.XmlNode node, Comic comic) {
  xml.XmlDocument doc = parse(node.text);
  doc.findElements("img").forEach((xml.XmlElement tag) {
    tag.attributes.forEach((att) {
      switch (att.name.local) {
        case "src":
          comic.src = att.value;
          break;
        case "title":
          comic.summary = att.value;
          break;
      }
    });
  });
}

void main() {
  runApp(new TFWCApp());
}

class TFWCApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new TFWCHomePage(title: 'That Famous Web Comic'),
    );
  }
}

class TFWCHomePage extends StatefulWidget {
  TFWCHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  TFWCHomePageState createState() => new TFWCHomePageState();
}

class TFWCHomePageState extends State<TFWCHomePage> {
  Comic selectedComic;
  List<DropdownMenuItem<Comic>> items;

  Widget build(BuildContext context) {
    var body;
    if (items == null) {
      body = new Center(child: new Text("Loading..."));
      _getSortedListOfComics().then((comics) {
        setState(() {
          items = new List<DropdownMenuItem<Comic>>();
          comics.forEach((comic) {
            var item = new DropdownMenuItem<Comic>(
                value: comic, child: new Text(comic.title));
            items.add(item);
            if (selectedComic == null) {
              selectedComic = comic;
            }
          });
        });
      });
    } else {
      final DropdownButton dropdownButton = new DropdownButton(
          items: items,
          value: selectedComic,
          onChanged: (s) {
            setState(() {
              selectedComic = s;
            });
          });
      final summary = new Padding(
          padding: new EdgeInsets.only(top: 16),
          child: new Text(
            selectedComic.summary,
            style: new TextStyle(fontSize: 18),
          ));
      final image = new SingleChildScrollView(
        child: new Image.network(selectedComic.src),
        scrollDirection: Axis.horizontal,
      );
      body = new Padding(
          padding: new EdgeInsets.fromLTRB(8, 0, 8, 8),
          child: new ListView(
            children: [
              new Flex(
                direction: Axis.vertical,
                children: <Widget>[dropdownButton, image, summary],
              )
            ],
          ));
    }
    return new Scaffold(
      appBar: new AppBar(
        title: new Text(widget.title),
      ),
      body: body,
    );
  }
}

class Comic {
  String title;
  String summary;
  String src;
}
