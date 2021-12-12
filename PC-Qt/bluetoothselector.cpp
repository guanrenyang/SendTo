#include "bluetoothselector.h"
#include "ui_bluetoothselector.h"

#include <QtBluetooth/qbluetoothlocaldevice.h>
#include <QtBluetooth/qbluetoothservicediscoveryagent.h>

QT_USE_NAMESPACE

BluetoothSelector::BluetoothSelector(const QBluetoothAddress &localAdapter, QWidget *parent)
    :   QDialog(parent), ui(new Ui::BluetoothSelector)
{
    ui->setupUi(this);

    m_discoveryAgent = new QBluetoothServiceDiscoveryAgent(localAdapter);

    connect(m_discoveryAgent, SIGNAL(serviceDiscovered(QBluetoothServiceInfo)),
            this, SLOT(serviceDiscovered(QBluetoothServiceInfo)));
    connect(m_discoveryAgent, SIGNAL(finished()), this, SLOT(discoveryFinished()));
    connect(m_discoveryAgent, SIGNAL(canceled()), this, SLOT(discoveryFinished()));

    this->setWindowTitle("蓝牙");
}

BluetoothSelector::~BluetoothSelector()
{
    delete ui;
    delete m_discoveryAgent;
}

void BluetoothSelector::startDiscovery(const QBluetoothUuid &uuid)
{
    ui->status->setText(tr("正在搜索..."));
    if (m_discoveryAgent->isActive())
        m_discoveryAgent->stop();

    ui->remoteDevices->clear();


    m_discoveryAgent->setUuidFilter(uuid);
    m_discoveryAgent->start(QBluetoothServiceDiscoveryAgent::FullDiscovery);

}

void BluetoothSelector::stopDiscovery()
{
    if (m_discoveryAgent){
        m_discoveryAgent->stop();
    }
}

QBluetoothServiceInfo BluetoothSelector::service() const
{
    return m_service;
}

void BluetoothSelector::serviceDiscovered(const QBluetoothServiceInfo &serviceInfo)
{
    QMapIterator<QListWidgetItem *, QBluetoothServiceInfo> i(m_discoveredServices);
    while (i.hasNext()){
        i.next();
        if (serviceInfo.device().address() == i.value().device().address()){
            return;
        }
    }

    QString remoteName;
    if (serviceInfo.device().name().isEmpty())
        remoteName = serviceInfo.device().address().toString();
    else
        remoteName = serviceInfo.device().name();

    QListWidgetItem *item =
        new QListWidgetItem(QString::fromLatin1("%1 %2").arg(remoteName,
                                                             serviceInfo.serviceName()));

    m_discoveredServices.insert(item, serviceInfo);
    ui->remoteDevices->addItem(item);
}

void BluetoothSelector::discoveryFinished()
{
    ui->status->setText(tr("请选择设备"));
}

void BluetoothSelector::on_remoteDevices_itemActivated(QListWidgetItem *item)
{
    qDebug() << "got click" << item->text();
    m_service = m_discoveredServices.value(item);
    if (m_discoveryAgent->isActive())
        m_discoveryAgent->stop();

    accept();
}

void BluetoothSelector::on_cancelButton_clicked()
{
    reject();
}
