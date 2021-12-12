#ifndef QTSOCKETCLIENT_H
#define QTSOCKETCLIENT_H

#include <QtWidgets/QWidget>

#include <QtNetwork>
#include <QHostAddress>
#include <QMessageBox>
#include <QDataStream>
#include <QByteArray>
#include <QDebug>
#include <QDateTime>
#include <QFile>
#include <QFileDialog>

class Client : public QWidget
{
    Q_OBJECT

public:
    Client(QWidget *parent = 0);
    ~Client();

//	void initTCP();
    void newConnect();
    QTcpSocket *fileSocket;
public slots:
    // 连接服务器
//	void connectServer();
    // 断开连接
//	void disconnectServer();
//	// 接收服务器发送的数据
//	void receiveData();
//	// 向服务器发送数据
//	void sendData();

    // 建立连接
    void buildConnection(QString ip);
    // 发送文件
    void sendFile(QString filePath);
    // 更新文件发送进度
    void updateFileProgress(qint64);
    // 更新文件接收进度
//	void updateFileProgress();

private:
//	Ui::ClientClass ui;

//	QTcpSocket *tcpSocket;


    // 文件传送
    QFile *localFile;
    // 文件大小
    qint64 totalBytes;      //文件总字节数
    qint64 bytesWritten;    //已发送的字节数
    qint64 bytestoWrite;    //尚未发送的字节数
    qint64 filenameSize;    //文件名字的字节数
    qint64 bytesReceived;   //接收的字节数
    // 每次发送数据大小
    qint64 perDataSize;
    QString filename;
    // 数据缓冲区
    QByteArray inBlock;
    QByteArray outBlock;

signals:
    void finishSendFile(); // 当前文件传输完成
    void updateProgress(qint64 value, qint64 total);
    void connectionBuilt();
    void connectionRefused();
};

#endif // QTSOCKETCLIENT_H
