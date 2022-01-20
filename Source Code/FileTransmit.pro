QT       += core gui widgets network opengl bluetooth

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

CONFIG += c++11

# You can make your code fail to compile if it uses deprecated APIs.
# In order to do so, uncomment the following line.
#DEFINES += QT_DISABLE_DEPRECATED_BEFORE=0x060000    # disables all the APIs deprecated before Qt 6.0.0

SOURCES += \
    bluetoothclient.cpp \
    bluetoothmodule.cpp \
    bluetoothselector.cpp \
    bluetoothserver.cpp \
    client.cpp \
    device.cpp \
    dragfileinterface.cpp \
    file.cpp \
    main.cpp \
    mainwindow.cpp \
    myudpclient.cpp \
    myudpserver.cpp \
    server.cpp

HEADERS += \
    bluetoothclient.h \
    bluetoothmodule.h \
    bluetoothselector.h \
    bluetoothserver.h \
    client.h \
    device.h \
    dragfileinterface.h \
    file.h \
    mainwindow.h \
    myudpclient.h \
    myudpserver.h \
    server.h


FORMS += \
    bluetoothselector.ui \
    device.ui \
    dragfileinterface.ui \
    file.ui \
    mainwindow.ui

RESOURCES += \
    StyleSheet.qrc \
    qdarkstyle/light/style.qrc \
    qdarkstyle/dark/style.qrc

RC_ICONS += icon.ico
TRANSLATIONS += \
    FileTransmit_zh_CN.ts
CONFIG += lrelease
CONFIG += embed_translations

# Default rules for deployment.
qnx: target.path = /tmp/$${TARGET}/bin
else: unix:!android: target.path = /opt/$${TARGET}/bin
!isEmpty(target.path): INSTALLS += target

DISTFILES += \
    SpyBot.qss
