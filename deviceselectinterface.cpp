#include "deviceselectinterface.h"
#include "ui_deviceselectinterface.h"

DeviceSelectInterface::DeviceSelectInterface(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::DeviceSelectInterface)
{
    ui->setupUi(this);
    ui->comboBox->addItem("My ipad");
}

DeviceSelectInterface::~DeviceSelectInterface()
{
    delete ui;
}
