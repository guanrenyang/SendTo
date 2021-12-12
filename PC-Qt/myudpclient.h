#ifndef MYUDPCLIENT_H
#define MYUDPCLIENT_H


#include <QtNetwork/QUdpSocket>
#include <QtNetwork/QNetworkInterface>
#include <QtNetwork/QHostAddress>
#include <QtNetwork/QHostInfo>

class MyUdpClient : public QObject
{
    Q_OBJECT

public:
    explicit MyUdpClient(QWidget *parent = 0);
    ~MyUdpClient();

    void BroadcastGetIpCommand();

    QHostAddress getIp();
public slots:
    void startBroadcast();
private:
    QUdpSocket * receiver;
    QUdpSocket * sender;
private slots:
    void processPengingDatagram();
signals:
    void getPeerIPandName(QString peerIp, QString peerName);
};

#endif // MYUDPCLIENT_H
