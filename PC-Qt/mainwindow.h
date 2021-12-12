#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QThread>
#include <device.h>
#include <file.h>
#include <dragfileinterface.h>
#include <QtWidgets>
#include <bluetoothmodule.h>
#include <QMetaType>
#include <QSystemTrayIcon>
#include "client.h"
#include "server.h"
#include "myudpclient.h"
#include "myudpserver.h"
#include "bluetoothselector.h"
#include "bluetoothserver.h"
#include "bluetoothclient.h"
QT_BEGIN_NAMESPACE
Q_DECLARE_METATYPE(QSet<QString>);
namespace Ui { class MainWindow; }



class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();

private:
    Ui::MainWindow *ui;
    QStackedLayout *stackedLayout;
    QComboBox *bluetoothComboxBox;
    QPushButton *bluetoothConnectButton;
    QLineEdit *myIpLineEdit;
    QLineEdit *peerIpLineEdit;
    QComboBox *udpComboxBox;
    QProgressBar *progressBar;

    QMenu *trayMenu;
    QSystemTrayIcon *tray;
    QAction *restoreAction;
    QAction *quitAction;

    Client * client;
    Server * server;

    MyUdpServer * udpServer;
    MyUdpClient * udpClient;

    DragFileInterface * dragFileInterface;

    qint64 numFileSent;
    qint64 numFileTotal;
    QString peerIP;
    QString myIP;
    QString myName;

    QHash<QString, QString> name2ip;
    QHash<QString, QString> ip2name;

    QList<BluetoothClient *> bluetoothClients;
    BluetoothServer * bluetoothServer;
private slots:
    int sendOneFile();
    void tcpMode();

    void addDeviceByUdp(QString peerIp, QString peerName);
    void removeDevice(QString  peerIp, QString peerName);

    void onBluetoothButtonClicked();
    void handleRefusedConnection();



};
#endif // MAINWINDOW_H
