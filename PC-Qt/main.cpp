#include "mainwindow.h"

#include <QApplication>
#include <QLocale>
#include <QTranslator>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    QFile f(":/SpyBot.qss");

    if (!f.exists())   {
        qDebug()<<"Unable to set stylesheet, file not found\n";
    }
    else   {
        f.open(QFile::ReadOnly | QFile::Text);
        QTextStream ts(&f);
        a.setStyleSheet(ts.readAll());
    }


    QTranslator translator;
    const QStringList uiLanguages = QLocale::system().uiLanguages();
    for (const QString &locale : uiLanguages) {
        const QString baseName = "FileTransmit_" + QLocale(locale).name();
        if (translator.load(":/i18n/" + baseName)) {
            a.installTranslator(&translator);
            break;
        }
    }
    MainWindow w;
    w.show();
    return a.exec();
}
