#ifndef SERVER_H
#define SERVER_H

#include <QtWidgets/QWidget>


#include <QtNetwork>
#include <QAbstractSocket>
#include <QTcpSocket>

#include <QFile>
#include <QFileDialog>

#include <QDateTime>
#include <QDataStream>
#include <QMessageBox>
class Server : public QWidget
{
    Q_OBJECT

public:
    Server(QWidget *parent = 0);
    ~Server();

    QTcpServer *fileserver;
    QTcpSocket *filesocket;
    QString peerIp;

private slots:
    void acceptFileConnection();
    void updateFileProgress();
    void displayError(QAbstractSocket::SocketError socketError);

private:


    qint64 totalBytes;
    qint64 bytesReceived;
    qint64 bytestoWrite;
    qint64 bytesWritten;
    qint64 filenameSize;
    QString filename;
    QString directory;

    qint64 perDataSize;
    QFile *localFile;

    QByteArray inBlock;
    QByteArray outBlock;

    QDateTime current_date_time;
    QString str_date_time;

    QDateTime start;
    QDateTime end;


signals:
    void updateProgress(qint64 value, qint64 total);
    void receiveFinish();
};

#endif // SERVER_H
