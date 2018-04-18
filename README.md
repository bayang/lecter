# lecter

An Inoreader Desktop App  
Still in early stage.

The app is destined to become a client for online aggregators like Inoreader, Commafeed etc...


## Layout

Two layouts available : 

* three pane view 
 

* grid view


And two themes for the moment a light one and a dark one.


## Content

For each article there are three contents : 

* the rss feed content

* the web article (from the original website)

* the cleaned up/mobile content (provided by The Mercury Web Parser https://mercury.postlight.com/)

The app supports internationalization, currently english and french are provided.

Translations are welcome !


## Services

Concerning Inoreader, Wallabag and Pocket, authentification and login are performed through Oauth2, so this app does **NOT** know and does **NOT** keep any of your credentials. 

We respect your privacy.

The articles can be shared to Pocket and Wallabag directly from the lecter.

Support for Instapaper is planned.


## Screenshots


You can see the different themes and layouts below : 


![grid view dark theme](screenshots/grid_dark.png)

![grid view dark theme with article](screenshots/grid_article_dark.png)

![grid view dark theme with article and pocket window](screenshots/grid_article_pocket_dark.png)

![list view dark theme with article](screenshots/list_dark.png)

![list view light theme with article](screenshots/list_light.png)

![list view light theme with mobile content article](screenshots/list_mobilizer_light.png)

![grid view light theme](screenshots/grid_article_light.png)

![settings light theme](screenshots/settings_light.png)

![inoreader login view light theme](screenshots/inoreader_login_light.png)


## Installation

At the moment we provide a .deb package and an appimage.

An appimage is a standalone, no installation, package which is supposed to run seamlessly on all Linux platforms.

To use it just set executable permission to the .appimage file and double-click/execute.

You will be prompted to see whether you want desktop integration or not.

Building .dmg and .exe should be possible once we find a build platform.


## Currently

Currently being rewritten after being forked from <https://github.com/PerfectDay20/ReadDay>

A renaming occured to reflect this and the app now lives its own life.


## Why the supid name ?

Well all RSS apps are called Feed or Reader or a combo of both.

In french the word reader is translated as lecteur and lecteur has the same pronounciation as lecter.

