#ifndef BluetoothClient_H
#define BluetoothClient_H

#include <QtCore/qobject.h>

#include <QtBluetooth/qbluetoothserviceinfo.h>
#include <QtBluetooth/qbluetoothsocket.h>

QT_FORWARD_DECLARE_CLASS(QBluetoothSocket)

QT_USE_NAMESPACE

class BluetoothClient : public QObject
{
    Q_OBJECT

public:
    explicit BluetoothClient(QObject *parent = nullptr);
    ~BluetoothClient();

    void startClient(const QBluetoothServiceInfo &remoteService);
    void stopClient();

public slots:
    void sendMessage(const QString &message);

signals:
    void messageReceived(const QString &sender, const QString &message);
    void connected(const QString &name);
    void disconnected();
    void socketErrorOccurred(const QString &errorString);

private slots:
    void readSocket();
    void connected();
    void onSocketErrorOccurred(QBluetoothSocket::SocketError);

private:
    QBluetoothSocket *socket = nullptr;
};

#endif
