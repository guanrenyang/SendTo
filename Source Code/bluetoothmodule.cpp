#include "bluetoothmodule.h"

BluetoothModule::BluetoothModule(QObject *parent) : QObject(parent)
{
    sendsocket = new QBluetoothSocket(QBluetoothServiceInfo::RfcommProtocol);
    receivesocket = new QBluetoothSocket(QBluetoothServiceInfo::RfcommProtocol);
    discoveryAgent = new QBluetoothDeviceDiscoveryAgent;
    connect(discoveryAgent, &QBluetoothDeviceDiscoveryAgent::deviceDiscovered, this, &BluetoothModule::discoverBlueTooth);
    connect(discoveryAgent, &QBluetoothDeviceDiscoveryAgent::finished, this, [=](){qDebug()<<"finished";});
    QString localHostName = QHostInfo::localHostName();
    QHostInfo info = QHostInfo::fromName(localHostName);
    foreach(QHostAddress address,info.addresses())
    {
         if(address.protocol() == QAbstractSocket::IPv4Protocol&&address.toString().right(2)!=".1")
             myip=address.toString();
    }
    qDebug()<<myip;

    discoveryAgent->start();

}
BluetoothModule::~BluetoothModule()
{

}
void BluetoothModule::acceptconnection(){
    this->receivesocket->abort();
    this->receivesocket=this->server->nextPendingConnection();

    qDebug()<<"bluetoothConnectedEvent";
    QByteArray arrayData;
    arrayData=myip.toUtf8();
    receivesocket->write(arrayData);
}
void BluetoothModule::connectToHost(QString deviceName){
    qDebug()<<"connect to host"<<deviceName<<device[deviceName];
    BTaddress = device[deviceName].toString();
    this->sendsocket->abort();
    static QString serviceUuid("e8e10f95-1a70-4b27-9ccf-02010264e9c8");
    if(BTaddress.size()!=0)
    {   qDebug()<<BTaddress;
        sendsocket->connectToService(QBluetoothAddress(BTaddress), QBluetoothUuid(serviceUuid),QIODevice::ReadWrite);
    }
    connect(sendsocket,&QBluetoothSocket::readyRead, this, &BluetoothModule::readBluetoothDataEvent);
}
void BluetoothModule::readBluetoothDataEvent()
{
   char data[100];
   qint64 len = sendsocket->read((char *)data, 100);
   QByteArray qa2((char*)data,len);
   itsip=qa2.toHex();
   emit getPeerIP(itsip);
   qDebug()<<"peer ip"<<itsip;
}
void BluetoothModule::bluetoothConnectedEvent()
{

}
void BluetoothModule::discoverBlueTooth(QBluetoothDeviceInfo information)
{
    device[information.name()]=information.address();
    emit findBluetoothDevice(information.name());
}
void BluetoothModule::chooseBlueTooth(QString name){
    //根据选择的设备名来赋值
    BTaddress=device[name].toString();

    qDebug()<<"choose blue tooth";
    this->server=new QBluetoothServer(QBluetoothServiceInfo::RfcommProtocol,this);
    this->server->listen();
    connect(server,&QBluetoothServer::newConnection,this,&BluetoothModule::acceptconnection);
}
