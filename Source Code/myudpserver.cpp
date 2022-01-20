 #include "myudpserver.h"

//#define GET_HOST_COMMAND "GetCYHost"
#define GET_HOST_COMMAND "GetIPAddr"
//#define LOCAL_PORT 11121
#define Server_PORT 12811

#define TRY_TIMES 1

MyUdpServer::MyUdpServer(QWidget *parent): QObject(parent)
{

}

MyUdpServer::~MyUdpServer()
{

}

void MyUdpServer::startBroadcastListener()
{
    receiver = new QUdpSocket(this);
    // 绑定，第一个参数为端口号，第二儿表示允许其它地址链接该广播
    receiver->bind(getIp(),Server_PORT,QUdpSocket::ShareAddress);
    //readyRead: 每当有数据报来时发送这个信号
    connect(receiver,SIGNAL(readyRead()),this,SLOT(processPengingDatagram()));

}

void MyUdpServer::processPengingDatagram()
{
    QHostAddress client_address;//声明一个QHostAddress对象用于保存发送端的信息
    //char buf[100];//声明一个字符数组用于接收发送过来的字符串
    //数据报不为空
    while( receiver->hasPendingDatagrams() )
    {
        quint16 recPort = 0;
        QByteArray datagram;
        //datagram大小为等待处理数据报的大小才能就收数据;
        datagram.resize( receiver->pendingDatagramSize() );
        //接收数据报
        receiver->readDatagram(datagram.data(),datagram.size(), &client_address, &recPort);
        //label->setText(datagram);
        QString strData= datagram;
        int ret = strData.compare(GET_HOST_COMMAND);
        if (0 == ret)
        {
            // 成功获得client_address
            qDebug()<<"get client IP address successfully"<<strData;
            emit getClientIp(strData);
            QByteArray datagback = getIp().toString().toLatin1()+" "+QHostInfo::localHostName().toLatin1();
            //QByteArray datagback = "my ip is:192.168.1.123";//+getIp().toLatin1();
            receiver->writeDatagram(datagback,datagback.size(),client_address,recPort);
        }
    }
}


//得到主机的ip地址
QHostAddress MyUdpServer::getIp()
{
 //使用allAddresses命令获得所有的ip地址
    QList<QHostAddress> list=QNetworkInterface::allAddresses();
    foreach (QHostAddress address,list)
    {
        if(address.protocol()==QAbstractSocket::IPv4Protocol&&address.toString().right(2)!=".1"){
            return address;
        }
    }
    return QHostAddress();
}
