import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:xml/xml.dart' as xml;
import 'dart:async';

import 'package:xml/xml.dart';

const String ATOM_URL = "https://www.xkcd.com/atom.xml";

Future<List<Comic>> _getSortedListOfComics() async {
  var result = <Comic>[];
  var httpClient = createHttpClient();
  var response = await httpClient.read(ATOM_URL);
  var document = xml.parse(response);
  var entries = document.findAllElements("entry");
  for (xml.XmlElement entry in entries) {
    Comic comic = new Comic();
    List<xml.XmlElement> tags = entry.children;
    tags.forEach((xml.XmlElement tag) {
      switch (tag.name.local) {
        case "title":
          comic.title = tag.text;
          break;
        case "summary":
          _parseSummary(tag.firstChild, comic);
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
      home: new TFWCHomePage(title: 'TFWC'),
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
    var child;
    if (items == null) {
      child = new Text("loading");
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
      DropdownButton dropdownButton = new DropdownButton(
          items: items,
          value: selectedComic,
          onChanged: (s) {
            setState(() {
              selectedComic = s;
            });
          });
      Text summary = new Text(selectedComic.summary);
      Image i = new Image.network(selectedComic.src);
      child = new ListView(
        children: <Widget>[dropdownButton, i, summary],
      );
//      child = new Column(
//        children: <Widget>[dropdownButton, summary],
//      );
    }
    return new Scaffold(
      appBar: new AppBar(
        title: new Text(widget.title),
      ),
      body: new Center(child: child),
    );
  }
}

class Comic {
  String title;
  String summary;
  String src;
}
