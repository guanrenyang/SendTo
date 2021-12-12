#include "dragfileinterface.h"
#include "ui_dragfileinterface.h"

DragFileInterface::DragFileInterface(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::DragFileInterface)
{
    ui->setupUi(this);

    setAcceptDrops(true);
    ui->fileListWidget->setDragEnabled(true);

    connect(ui->fileListWidget,SIGNAL(itemDoubleClicked(QListWidgetItem*)), this, SLOT(deleteFileByWidgetItem(QListWidgetItem*)));
}
void DragFileInterface::addFile(QString addr)
{

    QListWidgetItem *newFileItem = new QListWidgetItem(addr);

    listItem2File.insert(newFileItem, addr);
    file2ListItem.insert(addr, newFileItem);
    filenameSet.insert(addr);

    ui->fileListWidget->addItem(newFileItem);
}

void DragFileInterface::deleteFileByWidgetItem(QListWidgetItem *victim)
{
    QString victimFile = listItem2File.find(victim).value();
    filenameSet.erase(filenameSet.find(victimFile));
    listItem2File.erase(listItem2File.find(victim));
    file2ListItem.erase(file2ListItem.find(victimFile));

    delete ui->fileListWidget->takeItem(ui->fileListWidget->row(victim));
}
void DragFileInterface::deleteFileByName(QString victimFileName)
{
    QListWidgetItem *victimItem = file2ListItem.find(victimFileName).value();

    filenameSet.erase(filenameSet.find(victimFileName));
    file2ListItem.erase(file2ListItem.find(victimFileName));
    listItem2File.erase(listItem2File.find(victimItem));

    delete ui->fileListWidget->takeItem(ui->fileListWidget->row(victimItem));
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

            QListWidgetItem *newFileItem = new QListWidgetItem(addr);

            listItem2File.insert(newFileItem, addr);

            filenameSet.insert(addr);
            file2ListItem.insert(addr, newFileItem);

            test_set_hash();//DEBUG

            if (QFile(addr).exists()) // 判断地址是否有效
                ui->fileListWidget->addItem(newFileItem);
        }
    } else {
        event->ignore();
    }
    if(event->mimeData()->hasImage()){
        ui->fileListWidget->addItem("has icon");
    }
}
