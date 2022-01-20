#ifndef BLUETOOTHMODULE_H
#define BLUETOOTHMODULE_H

#include <QObject>
#include <QBluetoothDeviceDiscoveryAgent>
#include <QtBluetooth/QBluetoothDeviceInfo>
#include <QtBluetooth/QBluetoothLocalDevice>
#include <QtDebug>
#include <QtBluetooth/QBluetoothAddress>
#include <QtBluetooth/QBluetoothSocket>
#include <QtBluetooth/QBluetoothServer>
#include <QtBluetooth/QBluetoothServiceDiscoveryAgent>
#include <QtBluetooth/QBluetoothUuid>
#include <QIODevice>
#include <QtNetwork/QHostInfo>
#include <QtNetwork/QHostAddress>
#include <QtNetwork/QNetworkAddressEntry>
#include <QtNetwork/QNetworkInterface>
class BluetoothModule : public QObject
{
    Q_OBJECT
public:
    BluetoothModule(QObject *parent = nullptr);
    ~BluetoothModule();
    QBluetoothSocket *sendsocket;
    QBluetoothSocket *receivesocket;
    QBluetoothServer *server;
public slots:
    void chooseBlueTooth(QString name);
    void connectToHost(QString deviceName);
private slots:
    void discoverBlueTooth(QBluetoothDeviceInfo info);
    void readBluetoothDataEvent();
    void bluetoothConnectedEvent();
    void acceptconnection();

private:
    QBluetoothLocalDevice *localDevice;
    QString BTaddress;		// 记录MAC地址
    QString myip;
    QString itsip;
    QBluetoothDeviceDiscoveryAgent *discoveryAgent;
    QMap<QString,QBluetoothAddress> device;
signals:
    void findBluetoothDevice(QString deviceName);
    void getPeerIP(QString );
};

#endif // BLUETOOTHMODULE_H
