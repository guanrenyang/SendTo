#ifndef DRAGFILEINTERFACE_H
#define DRAGFILEINTERFACE_H

#include <QWidget>
#include <QtWidgets>
//#include "file.h"
QT_BEGIN_NAMESPACE
class QDragEnterEvent;
class QDropEvent;
QT_END_NAMESPACE

namespace Ui {
class DragFileInterface;
}

class DragFileInterface : public QWidget
{
    Q_OBJECT

public:
    QSet<QString> filenameSet;

    explicit DragFileInterface(QWidget *parent = nullptr);
    ~DragFileInterface();
    void addFile(QString);
//    QSet<QString> getFilenameSet();

public slots:
    void deleteFileByWidgetItem(QListWidgetItem *victim);
    void deleteFileByName(QString victimFileName);
protected:
    void dragEnterEvent(QDragEnterEvent *event) override;
    void dropEvent(QDropEvent *event) override;

private:
    Ui::DragFileInterface *ui;
//    QSet<File*> fileSet;
//    QHash<QListWidgetItem*, File*> listItem2File;
    QHash<QListWidgetItem*, QString> listItem2File;
    QHash<QString, QListWidgetItem*> file2ListItem;

    void test_set_hash()
    {
        qDebug()<<"FilenameSet";
        QSet<QString>::const_iterator i = filenameSet.constBegin();
        while(i!=filenameSet.constEnd())
        {
            qDebug()<< (*i) << ' ';
            ++i;
        }
        qDebug()<<" ";
        qDebug()<<"listItem2File";
        QHash<QListWidgetItem*, QString>::ConstIterator j = listItem2File.constBegin();
        while(j!=listItem2File.constEnd())
        {
            qDebug()<< j.key() << ' '<< j.value();
            ++j;
        }
        qDebug()<<" ";
        qDebug()<<"file2ListItem";
        QHash<QString, QListWidgetItem*>::ConstIterator k = file2ListItem.constBegin();
        while(k!=file2ListItem.constEnd())
        {
            qDebug()<< k.key() << ' '<< k.value();
            ++k;
        }
        qDebug()<<" ";
        qDebug()<<" ";
    }
};


#endif // DRAGFILEINTERFACE_H
