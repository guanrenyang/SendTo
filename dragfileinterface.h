#ifndef DRAGFILEINTERFACE_H
#define DRAGFILEINTERFACE_H

#include <QWidget>
#include <QtWidgets>
#include "file.h"
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
    explicit DragFileInterface(QWidget *parent = nullptr);
    ~DragFileInterface();
    void addFile(QString);

public slots:
    void deleteFile(QListWidgetItem *victim);
protected:
    void dragEnterEvent(QDragEnterEvent *event) override;
//    void dragMoveEvent(QDragMoveEvent *event) override;
//    void dragLeaveEvent(QDragLeaveEvent *event) override;
    void dropEvent(QDropEvent *event) override;
//    void mousePressEvent(QMouseEvent *event) override;

private:
    Ui::DragFileInterface *ui;
    QSet<File*> fileSet;
    QHash<QListWidgetItem*, File*> listItem2File;
};

#endif // DRAGFILEINTERFACE_H
