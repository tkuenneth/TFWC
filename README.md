# Welcome

That Famous WebComic (TFWC) is a viewer app for XKCD. It takes the atom feed of XKCD and displays 
the most recent episodes. XKCD is a webcomic by Randall Munroe. Please refer to http://xkcd.com/about/ 
for more details. TFWC is just an alternative way of browsing the comics. So, all credit for the 
comic itself goes to Randall Munroe.

The app is implemented using different technologies and platforms. These implementations reside in corresponding subdirectories. All are distributed under the terms of the GNU General Public licence v3 as published
by the Free Software Foundation.

## gluon

The app in the subdirectory *gluon* is based on Gluon Mobile. However, it deliberately does not use Gluon Glisten but a humble
JavaFX interface for the sake of exercise ;-). Also, the app does not use the JSON interface of
xkcd but relies on the atom feed, because this way a little more meta data is available.
