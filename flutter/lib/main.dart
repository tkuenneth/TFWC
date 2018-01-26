import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:xml/xml.dart' as xml;

final String ATOM_URL = "https://www.xkcd.com/atom.xml";

_getSortedListOfComics() async {
  var result = <Comic>[];
  var httpClient = createHttpClient();
  var response = await httpClient.read(ATOM_URL);
  var document = xml.parse(response);
  var entries = document.findAllElements("entry");
  for (xml.XmlNode node in entries) {
    var children = node.children;
    children.forEach((ch) => {});
  }
  return result;
}

void main() {
  runApp(new MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new MyHomePage(title: 'TFWC'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => new _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  Widget build(BuildContext context) {
    final dropdownMenuOptions = ["Hallo", "Du"]
        .toList()
        .map((String item) =>
            new DropdownMenuItem<String>(value: item, child: new Text(item)))
        .toList();

    _getSortedListOfComics();
    return new Scaffold(
      appBar: new AppBar(
        title: new Text(widget.title),
      ),
      body: new Center(
          child: new Column(children: [
        new DropdownButton(
            items: dropdownMenuOptions,
            onChanged: (s) {
              setState(() {});
            })
      ])),
    );
  }
}

class Comic {
  // Date updated = new Date();
  String summary = "<no summary>";
  String url = "<no url>";
  Image image = null;

  var titie;

  Comic(this.titie) {
  }
}
