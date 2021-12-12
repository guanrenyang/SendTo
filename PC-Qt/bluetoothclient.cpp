#include "bluetoothclient.h"

#include <QtCore/qmetaobject.h>

BluetoothClient::BluetoothClient(QObject *parent)
    :   QObject(parent)
{
}

BluetoothClient::~BluetoothClient()
{
    stopClient();
}

void BluetoothClient::startClient(const QBluetoothServiceInfo &remoteService)
{
    if (socket)
        return;

    // Connect to service
    socket = new QBluetoothSocket(QBluetoothServiceInfo::RfcommProtocol);
    qDebug() << "Create socket";
    socket->connectToService(remoteService);
    qDebug() << "ConnectToService done";

    connect(socket, &QBluetoothSocket::readyRead, this, &BluetoothClient::readSocket);
    connect(socket, &QBluetoothSocket::connected, this, QOverload<>::of(&BluetoothClient::connected));
    connect(socket, &QBluetoothSocket::disconnected, this, &BluetoothClient::disconnected);
    connect(socket, QOverload<QBluetoothSocket::SocketError>::of(&QBluetoothSocket::error),
            this, &BluetoothClient::onSocketErrorOccurred);

}


void BluetoothClient::stopClient()
{
    delete socket;
    socket = nullptr;
}

void BluetoothClient::readSocket()
{
    if (!socket)
        return;

    while (socket->canReadLine()) {
        QByteArray line = socket->readLine();
        emit messageReceived(socket->peerName(),
                             QString::fromUtf8(line.constData(), line.length()));
    }
}

void BluetoothClient::sendMessage(const QString &message)
{
    QByteArray text = message.toUtf8() + '\n';
    socket->write(text);
}


void BluetoothClient::onSocketErrorOccurred(QBluetoothSocket::SocketError error)
{
    if (error == QBluetoothSocket::NoSocketError)
        return;

    QMetaEnum metaEnum = QMetaEnum::fromType<QBluetoothSocket::SocketError>();
    QString errorString = socket->peerName() + QLatin1Char(' ')
            + metaEnum.valueToKey(error) + QLatin1String(" occurred");

    emit socketErrorOccurred(errorString);
}


void BluetoothClient::connected()
{
    emit connected(socket->peerName());
}

