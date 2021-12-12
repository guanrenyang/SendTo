#include "myudpclient.h"

//#define GET_HOST_COMMAND "GetCYHost"
#define GET_HOST_COMMAND "GetIPAddr"
#define LOCAL_PORT 11121
#define DEST_PORT 12811

#define TRY_TIMES 1

MyUdpClient::MyUdpClient(QWidget *parent)
{

}

MyUdpClient::~MyUdpClient()
{

}

void MyUdpClient::startBroadcast()
{
    receiver = new QUdpSocket(this);
    /////绑定，第一个参数为端口号，第二儿表示允许其它地址链接该广播
    receiver->bind(getIp(),LOCAL_PORT,QUdpSocket::ShareAddress);

    //readyRead:每当有数据报来时发送这个信号
    connect(receiver,SIGNAL(readyRead()),this,SLOT(processPengingDatagram()));
    qDebug()<<777;
    BroadcastGetIpCommand();
}

void MyUdpClient::BroadcastGetIpCommand()
{
    //QByteArray datagram = "Hello World!";
    QByteArray datagram = GET_HOST_COMMAND;
    qDebug()<<888;
    int times = TRY_TIMES;
    while(times--)
    {
        //sender->writeDatagram(datagram.data(),datagram.size(),QHostAddress::Broadcast,1066);
        receiver->writeDatagram(datagram.data(),datagram.size(),QHostAddress::Broadcast,DEST_PORT);
    }
}


void MyUdpClient::processPengingDatagram()
{
    //数据报不为空
    qDebug()<<999;
    while( receiver->hasPendingDatagrams() )
    {   qDebug()<<555;
        QByteArray datagram;
        //datagram大小为等待处理数据报的大小才能就收数据;
        datagram.resize( receiver->pendingDatagramSize() );
        //接收数据报
        receiver->readDatagram(datagram.data(),datagram.size());
        //label->setText(datagram);
//        addIpItem(datagram);
        QString peerIp = datagram;
        QStringList peerIpName =  peerIp.split(' ');
        //GRY: 将对方IP和设备名传输
        emit getPeerIPandName(peerIpName[0], peerIpName[1]);
    }
}

QHostAddress MyUdpClient::getIp()
{
 //使用allAddresses命令获得所有的ip地址
    QList<QHostAddress> list=QNetworkInterface::allAddresses();
    foreach (QHostAddress address,list)
    {
        if(address.protocol()==QAbstractSocket::IPv4Protocol&&address.toString().right(1)!="1"){
            return address;
        }
    }
}

