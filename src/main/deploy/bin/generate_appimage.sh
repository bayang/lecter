#!/bin/sh

echo "in post build script $(pwd)"
VERSION="$1"

#cat <<EOT >> ../jfx/native/lecter/app/lecter.cfg
#[JVMOptions]
#-Xmx1g
#-Djava.net.preferIPv4Stack=true
#-Dspring.config.location=classpath:application.properties,classpath:/reader_config/reader.properties,file:\${user.home}/.config/lecter/config.properties
#-Dlecter.log.dir=\${user.home}/.config/lecter/
#EOT

cp -R ../jfx/native/lecter/* .
cp ../../src/main/deploy/bin/desktopintegration lecter.wrapper
chmod +x lecter.wrapper
mkdir -p usr/share/icons/hicolor/
mkdir -p usr/share/icons/default/
cp ../../src/main/deploy/package/appimage/lecter.desktop .
cp ../../src/main/deploy/package/linux/lecter.png .
cp -R ../../src/main/deploy/icons/* usr/share/icons/hicolor/
cp -R ../../src/main/deploy/icons/* usr/share/icons/default/
mkdir -p usr/share/metainfo/
cp ../../src/main/package/linux/lecter.appdata.xml usr/share/metainfo/
ln -s lecter.wrapper AppRun

cd ..
echo "downloading appimage executable"
wget https://github.com/AppImage/AppImageKit/releases/download/10/appimagetool-x86_64.AppImage -O appimagetool-x86_64.AppImage
chmod +x appimagetool-x86_64.AppImage
echo "generating lecter appimage"
./appimagetool-x86_64.AppImage -nv lecter.AppDir lecter-$VERSION.AppImage

exit 0
