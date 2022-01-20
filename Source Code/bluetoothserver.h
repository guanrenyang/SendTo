#ifndef BLUETOOTHSERVER_H
#define BLUETOOTHSERVER_H

#include <QtCore/qobject.h>

#include <QtBluetooth/qbluetoothaddress.h>
#include <QtBluetooth/qbluetoothserviceinfo.h>

QT_FORWARD_DECLARE_CLASS(QBluetoothServer)
QT_FORWARD_DECLARE_CLASS(QBluetoothSocket)

QT_USE_NAMESPACE

class BluetoothServer : public QObject
{
    Q_OBJECT

public:
    explicit BluetoothServer(QObject *parent = nullptr);
    ~BluetoothServer();

    void startServer(const QBluetoothAddress &localAdapter = QBluetoothAddress());
    void stopServer();

public slots:
    void sendMessage(const QString &message);

signals:
    void messageReceived(const QString &sender, const QString &message);
    void clientConnected(const QString &name);
    void clientDisconnected(const QString &name);

private slots:
    void clientConnected();
    void clientDisconnected();
    void readSocket();

private:
    QBluetoothServer *rfcommServer = nullptr;
    QBluetoothServiceInfo serviceInfo;
    QList<QBluetoothSocket *> clientSockets;
};

#endif
