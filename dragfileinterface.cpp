#include "dragfileinterface.h"
#include "ui_dragfileinterface.h"

DragFileInterface::DragFileInterface(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::DragFileInterface)
{
    ui->setupUi(this);

    setAcceptDrops(true);
    ui->fileListWidget->setDragEnabled(true);

    connect(ui->fileListWidget,SIGNAL(itemDoubleClicked(QListWidgetItem*)), this, SLOT(deleteFile(QListWidgetItem*)));
}
void DragFileInterface::addFile(QString addr)
{
    File *newFile = new File(addr, addr);
    QListWidgetItem *newFileItem = new QListWidgetItem(addr);

    listItem2File.insert(newFileItem, newFile);
    fileSet.insert(newFile);

    ui->fileListWidget->addItem(newFileItem);
}
void DragFileInterface::deleteFile(QListWidgetItem *victim)
{
//    ui->fileListWidget->addItem(victim->text());
    File *victimFile = *listItem2File.find(victim);
    fileSet.erase(fileSet.find(victimFile));
    delete ui->fileListWidget->takeItem(ui->fileListWidget->row(victim));

}
DragFileInterface::~DragFileInterface()
{
    delete ui;
}

void DragFileInterface::dragEnterEvent(QDragEnterEvent *event)
{
    if(event->mimeData()->hasText())
        event->acceptProposedAction();
    else{
        event->ignore();
    }
}

void DragFileInterface::dropEvent(QDropEvent *event)
{
    if (event->mimeData()->hasText()) {
        QStringList pieces = event->mimeData()->text().split('\n');
        for(auto addr :pieces)
        {
            addr.remove(0,8);// 地址读进来开头会有"file:///"需要删掉

            File *newFile = new File(addr, addr);
            QListWidgetItem *newFileItem = new QListWidgetItem(addr);

            listItem2File.insert(newFileItem, newFile);
            fileSet.insert(newFile);

            if (QFile(addr).exists()) // 判断地址是否有效
                ui->fileListWidget->addItem(addr);
//            else
//            {
//                QDialog *fileErrorDialog = new QDialog();
//                QLabel *errorMessage = new QLabel(fileErrorDialog);
//                errorMessage->setText("Path Error");
//                fileErrorDialog->exec();
//            }
        }
    } else {
        event->ignore();
    }
    if(event->mimeData()->hasImage()){
        ui->fileListWidget->addItem("has icon");
    }
}
