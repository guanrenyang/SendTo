#include "client.h"

Client::Client(QWidget *parent)
    : QWidget(parent)
{

    // 文件传送相关变量初始化
    // 每次发送数据大小为64kb
    perDataSize = 64 * 1024;
    totalBytes = 0;
    bytestoWrite = 0;
    bytesWritten = 0;
    bytesReceived = 0;
    filenameSize = 0;

}

Client::~Client()
{

}


void Client::buildConnection(QString ip)
{
    this->fileSocket = new QTcpSocket(this);
    fileSocket->abort();
    fileSocket->connectToHost(ip, 8888);
    // 文件传送进度更新
    connect(fileSocket, SIGNAL(bytesWritten(qint64)), this, SLOT(updateFileProgress(qint64)));
    // 处理服务器返回的接收状态
    connect(fileSocket, &QTcpSocket::readyRead, this, [=](){
        int errorCode = QString::fromLatin1(fileSocket->readAll()).toInt();
        qDebug()<<"errorCode"<<errorCode;
        switch (errorCode) {
            case 0: emit finishSendFile();break;
            case 1: emit connectionBuilt();break;
            case 2: emit connectionRefused();break;
        }
    });
}

void Client::sendFile(QString filePath)
{
    this->filename = filePath;

    this->localFile = new QFile(filename);
    if (!localFile->open(QFile::ReadOnly))
        return;
    // 获取文件大小
    this->totalBytes = localFile->size();
    QDataStream sendout(&outBlock, QIODevice::WriteOnly);
    sendout.setVersion(QDataStream::Qt_4_8);
    QString currentFileName = filename.right(filename.size() - filename.lastIndexOf('/') - 1);

    // 保留总代大小信息空间、文件名大小信息空间、文件名
    sendout << qint64(0) << qint64(0) << currentFileName;
    totalBytes += outBlock.size();
    sendout.device()->seek(0);
    sendout << totalBytes << qint64((outBlock.size() - sizeof(qint64)* 2));

    bytestoWrite = totalBytes - fileSocket->write(outBlock);
    outBlock.resize(0);
}

void Client::updateFileProgress(qint64 numBytes)
{
    // 已经发送的数据大小
    bytesWritten += (int)numBytes;

    // 如果已经发送了数据
    if (bytestoWrite > 0)
    {
        outBlock = localFile->read(qMin(bytestoWrite, perDataSize));
        // 发送完一次数据后还剩余数据的大小
        bytestoWrite -= ((int)fileSocket->write(outBlock));
        // 清空发送缓冲区
        outBlock.resize(0);
    }
    else
        localFile->close();

    // 更新进度条

    emit updateProgress(bytesWritten, totalBytes);
    qDebug()<<bytesWritten<<totalBytes;
    // 如果发送完毕
    if (bytesWritten == totalBytes)
    {
        localFile->close();
    }
}
