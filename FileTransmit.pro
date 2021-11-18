QT       += core gui widgets

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

CONFIG += c++11

# You can make your code fail to compile if it uses deprecated APIs.
# In order to do so, uncomment the following line.
#DEFINES += QT_DISABLE_DEPRECATED_BEFORE=0x060000    # disables all the APIs deprecated before Qt 6.0.0

SOURCES += \
    device.cpp \
    deviceselectinterface.cpp \
    dragfileinterface.cpp \
    file.cpp \
    main.cpp \
    mainwindow.cpp

HEADERS += \
    device.h \
    deviceselectinterface.h \
    dragfileinterface.h \
    file.h \
    mainwindow.h

FORMS += \
    device.ui \
    deviceselectinterface.ui \
    dragfileinterface.ui \
    file.ui \
    mainwindow.ui

TRANSLATIONS += \
    FileTransmit_zh_CN.ts
CONFIG += lrelease
CONFIG += embed_translations

# Default rules for deployment.
qnx: target.path = /tmp/$${TARGET}/bin
else: unix:!android: target.path = /opt/$${TARGET}/bin
!isEmpty(target.path): INSTALLS += target
