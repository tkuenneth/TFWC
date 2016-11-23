# Welcome

That Famous WebComic (TFWC) is a viewer app for XKCD. It takes the atom feed of XKCD and displays 
the most recent episodes. XKCD is a webcomic by Randall Munroe. Please refer to http://xkcd.com/about/ 
for more details. This app is just an alternative way of browsing the comics. So, all credit for the 
comic itself goes to Randall Munroe.

The app is distributed under the terms of the GNU General Public licence v3 as published
by the Free Software Foundation.

## Technical stuff

The app is based on Gluon Mobile. However, it deliberately does not use Gluon Glisten but a humble
JavaFX interface for the sake of exercise ;-). Also, the app does not use the JSON interface of
xkcd but relys on the atom feed, because this way a little more meta data is available.
