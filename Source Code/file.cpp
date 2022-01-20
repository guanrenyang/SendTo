#include "file.h"
#include "ui_file.h"

File::File(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::File)
{
    ui->setupUi(this);
}

File::File(QString name, QString absolute_address, QWidget *parent):
    QWidget(parent),
    ui(new Ui::File)
{
    ui->setupUi(this);
    this->name = name;
    this->absolute_address = absolute_address;
}
File::~File()
{
    delete ui;
}
QString File::getFileName()
{
    return ui->fileName->text();
}
QString File::getFileAddress()
{
    return absolute_address;
}

