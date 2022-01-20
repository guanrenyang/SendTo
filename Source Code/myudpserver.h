#ifndef MYUDPSERVER_H
#define MYUDPSERVER_H


#include <QtNetwork/QHostAddress>
#include <QtNetwork/QHostInfo>
#include <QtNetwork/QUdpSocket>
#include <QtNetwork/QNetworkInterface>
#include <QLabel>
#include <QListWidget>
#include <QVBoxLayout>
#include <QListWidgetItem>
class QLabel;
class QUdpSocket;
class QListWidget;
class MyUdpServer: public QObject
{
  Q_OBJECT

public:
  explicit MyUdpServer(QWidget *parent = 0);
  ~MyUdpServer();

  void startBroadcastListener();
  QHostAddress getIp();

private:
  QUdpSocket * receiver;
  QUdpSocket * sender;
private slots:
  void processPengingDatagram();
signals:
  void getClientIp(QString clientIP);
};

#endif // MYUDPSERVER_H
