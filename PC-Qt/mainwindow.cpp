#include "mainwindow.h"
#include "ui_mainwindow.h"

static const QLatin1String serviceUuid("e8e10f95-1a70-4b27-9ccf-02010264e9c8");
MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    ui->udpButton->setFixedHeight(ui->udpButton->height()*2);
    ui->bluetoothButton->setFixedHeight(ui->bluetoothButton->height()*2);
    ui->tcpButton->setFixedHeight(ui->tcpButton->height()*2);

    progressBar = new QProgressBar(this);
    progressBar->setValue(0);
    progressBar->setFormat(QString("%1/%2").arg(0).arg(0));
    progressBar->setVisible(true);

    dragFileInterface = new DragFileInterface(this);

    // find my ip
    QString localHostName = QHostInfo::localHostName();
    QHostInfo info = QHostInfo::fromName(localHostName);
    foreach(QHostAddress address,info.addresses())
    {
         if(address.protocol() == QAbstractSocket::IPv4Protocol&&address.toString().right(2)!=".1")
         {
             this->myIP = address.toString();
             break;
         }
    }
    this->myName = QHostInfo::localHostName();

    this->setWindowTitle("SendTo");

    // set bluetooth layout

    QWidget *bluetoothWidget = new QWidget(this);
    QVBoxLayout *bluetoothLayout = new QVBoxLayout;
    bluetoothWidget->setLayout(bluetoothLayout);

    QLabel *bluetoothLabel = new QLabel("请选择设备", bluetoothWidget);
//    bluetoothComboxBox = new QComboBox(bluetoothWidget);
    bluetoothConnectButton = new QPushButton("蓝牙连接", bluetoothWidget);
    bluetoothConnectButton->setFixedHeight(bluetoothConnectButton->height()*2);
    bluetoothLayout->addWidget(bluetoothLabel);
//    bluetoothLayout->addWidget(bluetoothComboxBox);
    bluetoothLayout->addWidget(bluetoothConnectButton);
    bluetoothLayout->addStretch();


    // set udp layout
    QWidget *udpWidget = new QWidget(this);
    QVBoxLayout *udpLayout = new QVBoxLayout;
    udpWidget->setLayout(udpLayout);

    QLabel *udpLabel = new QLabel("请选择局域网设备");
    udpComboxBox = new QComboBox(udpWidget);
    udpComboxBox->setFixedHeight(udpComboxBox->height());
    QPushButton *udpSearch = new QPushButton("重新扫描",udpWidget);
    udpSearch->setFixedHeight(udpSearch->height()*2);
    udpLayout->addWidget(udpLabel);
    udpLayout->addWidget(udpComboxBox);
    udpLayout->addWidget(udpSearch);
    udpLayout->addStretch();

    // set tcp layout
    QWidget *tcpWidget = new QWidget(this);
    QVBoxLayout *tcpLayout = new QVBoxLayout;
    tcpWidget->setLayout(tcpLayout);

    QLabel *myIpLabel = new QLabel("本机地址", tcpWidget);
    myIpLineEdit = new QLineEdit(tcpWidget);
    myIpLineEdit->setReadOnly(true);
    QLabel *peerIplabel = new QLabel("目的地址",tcpWidget);
    peerIpLineEdit = new QLineEdit(tcpWidget);
    tcpLayout->addWidget(myIpLabel);
    tcpLayout->addWidget(myIpLineEdit);
    tcpLayout->addWidget(peerIplabel);
    tcpLayout->addWidget(peerIpLineEdit);

    stackedLayout = new QStackedLayout;

    stackedLayout->addWidget(udpWidget);
    stackedLayout->addWidget(bluetoothWidget);
    stackedLayout->addWidget(tcpWidget);

    ui->verticalLayout->addLayout(stackedLayout);
    ui->verticalLayout->addStretch();

    ui->verticalLayout_3->insertWidget(0, dragFileInterface);
    ui->verticalLayout_3->insertWidget(1, progressBar);


    // set page switch
    connect(ui->udpButton, &QPushButton::clicked, this, [=](){stackedLayout->setCurrentIndex(0);});
    connect(ui->bluetoothButton, &QPushButton::clicked, this, [=](){
        stackedLayout->setCurrentIndex(1);
    });
    connect(ui->tcpButton, &QPushButton::clicked, this, &MainWindow::tcpMode);

    connect(ui->selectFileButton,&QPushButton::clicked,[=](){
        QFileDialog selectFileDialog(this);
        QStringList fileNames;
        if (selectFileDialog.exec())
             fileNames = selectFileDialog.selectedFiles();
        for(auto addr: fileNames)
            dragFileInterface->addFile(addr);
    });

    // minimize to tray
    tray= new QSystemTrayIcon(this);//初始化托盘对象tray
    tray->setIcon(QIcon());//设定托盘图标，引号内是自定义的png图片路径
    tray->setToolTip("SendTo");
    QString title="APP Message";

    tray->show();//让托盘图标显示在系统托盘上
    tray->showMessage(title,"SendTo",QSystemTrayIcon::Information,3000); //最后一个参数为提示时长，默认10000，即10s

    restoreAction = new QAction("Open", this);
    connect(restoreAction, SIGNAL(triggered()), this, SLOT(show()));
    quitAction = new QAction("Quit", this);
    connect(quitAction, SIGNAL(triggered()), qApp, SLOT(quit()));

    trayMenu = new QMenu(this);
    trayMenu->addAction(restoreAction);
    trayMenu->addSeparator();
    trayMenu->addAction(quitAction);
    tray->setContextMenu(trayMenu);
    connect(tray,&QSystemTrayIcon::activated,this,[=](QSystemTrayIcon::ActivationReason reason){
        switch(reason)
            {
            case QSystemTrayIcon::Trigger://单击托盘图标
                break;
            case QSystemTrayIcon::DoubleClick://双击托盘图标
                this->showNormal();
                break;
            default:
                break;
            }
    });
    // set bluetooth module
    connect(bluetoothConnectButton, &QPushButton::clicked, this, &MainWindow::onBluetoothButtonClicked);
    // set bluetooth server
    bluetoothServer = new BluetoothServer(this);
    connect(bluetoothServer, QOverload<const QString &>::of(&BluetoothServer::clientConnected), this, [=](QString name){qDebug()<<name;});
    connect(bluetoothServer, QOverload<const QString &>::of(&BluetoothServer::clientDisconnected), this, [=](){
        BluetoothClient *bluetoothClient = qobject_cast<BluetoothClient *>(sender());
        if (bluetoothClient) {
            bluetoothClients.removeOne(bluetoothClient);
            client->deleteLater();
        }
    });
    connect(bluetoothServer, &BluetoothServer::messageReceived, this, [=](const QString sender, const QString message){
        bluetoothServer->sendMessage(this->myIP);
    });
    bluetoothServer->startServer();


    // set tcp client and server for transferring files
    client = new Client(this);
    server = new Server(this);

//    connect(client, &Client::finishSendFile, this, &MainWindow::sendOneFile);
    connect(client, &Client::connectionBuilt, this, &MainWindow::sendOneFile);
    connect(client, &Client::connectionRefused, this, &MainWindow::handleRefusedConnection);
    connect(client, &Client::finishSendFile, this, [=](){
        progressBar->setValue(0);
        numFileSent++;
        this->sendOneFile();
    });

//    connect(server, &Server::receiveFinish, this, [=](){
//        progressBar->setValue(0);
//    });

    // set udp client and server for find surrouding devices
    udpServer = new MyUdpServer(this);
    udpClient = new MyUdpClient(this);

    udpServer->startBroadcastListener();
    udpClient->startBroadcast();
    connect(udpSearch, &QPushButton::clicked, udpClient, &MyUdpClient::startBroadcast);
    connect(udpClient, &MyUdpClient::getPeerIPandName, this, &MainWindow::addDeviceByUdp);
    connect(udpComboxBox, QOverload<const QString &>::of(&QComboBox::activated), this, [=](QString peerName){
        this->peerIP = name2ip[peerName];
        server->peerIp = this->peerIP;
        qDebug()<<this->peerIP;
    });


    // update progress bar
    connect(client, &Client::updateProgress, this, [=](qint64 value, qint64 total){
       progressBar->setValue(value) ;
       progressBar->setMaximum(total);
       progressBar->setFormat(QString("%1/%2").arg(numFileSent+1).arg(numFileTotal));
    });
    connect(server, &Server::updateProgress, this, [=](qint64 value, qint64 total){
       progressBar->setValue(value) ;
       progressBar->setMaximum(total);
    });

    // build tcp connection and start send files
    connect(ui->sendFileButton, &QPushButton::clicked, this, [=](){
        numFileTotal = dragFileInterface->filenameSet.size();
        numFileSent = 0;

        // when send a new file, refresh progress bar

        progressBar->setValue(0);

//        ui->sendFileButton->setEnabled(false);
        qDebug()<<this->peerIP;
        client->buildConnection(this->peerIP);
    });

}
int MainWindow::sendOneFile(){
    qDebug()<<(dragFileInterface->filenameSet).empty();
    if(!(dragFileInterface->filenameSet).empty())
    {
        QSet<QString>::const_iterator iter = (dragFileInterface->filenameSet).begin();
        client->sendFile(*iter);

        dragFileInterface->deleteFileByName(*iter);
    }
    else
    {
//        ui->sendFileButton->setEnabled(true);
    }
    return 0;
}
void MainWindow::tcpMode()
{
    stackedLayout->setCurrentIndex(2);

    myIpLineEdit->setText(this->myIP);

    connect(peerIpLineEdit, &QLineEdit::editingFinished, this, [=](){
        QHostAddress newAddress(peerIpLineEdit->text());
        if(newAddress.protocol()==QAbstractSocket::IPv4Protocol)
        {
            this->peerIP=newAddress.toString();
            server->peerIp = this->peerIP;
        }
        else if(!peerIpLineEdit->text().isEmpty())
        {
            peerIpLineEdit->clear();
        }
    });

}
void MainWindow::addDeviceByUdp(QString peerIp, QString peerName)
{

    if(name2ip.find(peerName)!=name2ip.end() || peerIp==this->myIP || peerName == myName )
        return;
    name2ip.insert(peerName, peerIp);
    ip2name.insert(peerIP, peerName);

    udpComboxBox->addItem(peerName);

}
void MainWindow::removeDevice(QString  peerIp, QString peerName)
{
    ip2name.erase(ip2name.find(peerIP));
    name2ip.erase(name2ip.find(peerName));
}
void MainWindow::onBluetoothButtonClicked()
{
    bluetoothConnectButton->setEnabled(false);

    //scan for services
    const QBluetoothAddress adapter = QBluetoothAddress();

    BluetoothSelector remoteSelector(adapter);

    remoteSelector.startDiscovery(QBluetoothUuid(serviceUuid));

    if(remoteSelector.exec() == QDialog::Accepted)
    {
        QBluetoothServiceInfo service = remoteSelector.service();

        qDebug() << "Connecting to service 2" << service.serviceName()
                 << "on" << service.device().name();

        // Create client
        qDebug() << "Going to create client";

        BluetoothClient *bluetoothClient = new BluetoothClient(this);
        qDebug() << "Connecting...";

        connect(bluetoothClient, &BluetoothClient::messageReceived, this, [=](QString sender, QString peerIP){this->peerIP=peerIP;});
        connect(bluetoothClient, &BluetoothClient::disconnected, this, [=](){
            BluetoothClient *bluetoothClient = qobject_cast<BluetoothClient *>(sender());
            if (bluetoothClient) {
                bluetoothClients.removeOne(bluetoothClient);
                bluetoothClient->deleteLater();
            }
        });
        connect(bluetoothClient, QOverload<const QString &>::of(&BluetoothClient::connected), this, [=](){bluetoothClient->sendMessage(this->myIP);});
        qDebug() << "Start client";
        bluetoothClient->startClient(service);

        bluetoothClients.append(bluetoothClient);
    }
    bluetoothConnectButton->setEnabled(true);
}
void MainWindow::handleRefusedConnection()
{
    progressBar->setFormat(QString("%1/%2").arg(numFileSent).arg(numFileTotal));
    progressBar->setValue(0);
    QMessageBox::information(this, "连接失败", "你的发送请求被对方拒绝");
}
MainWindow::~MainWindow()
{
    delete ui;
}


