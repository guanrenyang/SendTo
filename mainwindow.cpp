#include "mainwindow.h"
#include "ui_mainwindow.h"
MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{


    ui->setupUi(this);

    DeviceSelectInterface * deviceSelectInterface = new DeviceSelectInterface();
    DragFileInterface * dragFileInterface = new DragFileInterface();

    ui->verticalLayout->addWidget(deviceSelectInterface);
    ui->verticalLayout->addWidget(dragFileInterface);

    connect(ui->selectFileButton,&QPushButton::clicked,[=](){
        QFileDialog selectFileDialog(this);
        QStringList fileNames;
        if (selectFileDialog.exec())
             fileNames = selectFileDialog.selectedFiles();
        for(auto addr: fileNames)
            dragFileInterface->addFile(addr);

    });
}

MainWindow::~MainWindow()
{
    delete ui;
}

