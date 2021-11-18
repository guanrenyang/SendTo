#include "device.h"
#include "ui_device.h"

Device::Device(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::Device)
{
    ui->setupUi(this);
}

Device::~Device()
{
    delete ui;
}
