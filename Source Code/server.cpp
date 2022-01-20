#include "server.h"
#include <QMessageBox>
#include <QString>
#include <QByteArray>

Server::Server(QWidget *parent)
    : QWidget(parent)
{

    // 文件传送套接字
    this->filesocket = new QTcpSocket(this);
    this->fileserver = new QTcpServer(this);

    this->fileserver->listen(QHostAddress::Any, 8888);
    connect(this->fileserver, SIGNAL(newConnection()), this, SLOT(acceptFileConnection()));

    // 文件传送相关变量初始化
    bytesReceived = 0;
    totalBytes = 0;
    filenameSize = 0;
}

Server::~Server()
{

}


void Server::acceptFileConnection()
{
    bytesWritten = 0;
    this->filesocket = this->fileserver->nextPendingConnection();

    if(this->filesocket->peerAddress().isEqual(QHostAddress(this->peerIp)))
        return;
    int ret = QMessageBox::question(this,"Ask", "Accept new request?",  QMessageBox::Save|QMessageBox::Cancel);
    if (ret==QMessageBox::Save)
    {
        connect(filesocket, SIGNAL(readyRead()), this, SLOT(updateFileProgress()));
        qDebug()<<"accept connection";
        start = QDateTime::currentDateTime();
        // 接受文件

        directory = QFileDialog::getExistingDirectory()+"/"+filename;
        this->filesocket->write(QString::number(1).toLatin1());

    }
    else if (ret==QMessageBox::Cancel)
    {
        this->filesocket->write(QString::number(2).toLatin1());
        disconnect(filesocket, SIGNAL(readyRead()), this, SLOT(updateFileProgress()));
        this->filesocket->abort();
        this->fileserver->disconnect();
    }



}

void Server::updateFileProgress()
{
    QDataStream inFile(this->filesocket);
    inFile.setVersion(QDataStream::Qt_4_8);

    // 如果接收到的数据小于16个字节，保存到来的文件头结构
    if (bytesReceived <= sizeof(qint64)* 2)
    {
        if ((filesocket->bytesAvailable() >= (sizeof(qint64)) * 2) && (filenameSize == 0))
        {
            inFile >> totalBytes >> filenameSize;
            bytesReceived += sizeof(qint64)* 2;
        }
        if ((filesocket->bytesAvailable() >= filenameSize) && (filenameSize != 0))
        {
            inFile >> filename;
            bytesReceived += filenameSize;
            localFile = new QFile(directory+filename);
            qDebug()<<directory;
            if (!localFile->open(QFile::WriteOnly))
            {
                qDebug() << "Server::open file error!";
                return;
            }
        }
        else
            return;
    }
    // 如果接收的数据小于总数据，则写入文件
    if (bytesReceived < totalBytes)
    {
        bytesReceived += filesocket->bytesAvailable();
        inBlock = filesocket->readAll();
        localFile->write(inBlock);
        inBlock.resize(0);
    }
    // 更新进度条显示

    emit updateProgress(bytesReceived, totalBytes);

    // 数据接收完成时
    if (bytesReceived == totalBytes)
    {

        this->filesocket->write(QString::number(0).toLatin1());

        bytesReceived = 0;
        totalBytes = 0;
        filenameSize = 0;
        localFile->close();
        emit receiveFinish();
        qDebug()<<start.secsTo(QDateTime::currentDateTime());
    }
}

void Server::displayError(QAbstractSocket::SocketError socketError)
{
    qDebug() << filesocket->errorString();
    filesocket->close();
}

