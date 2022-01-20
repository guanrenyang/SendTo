#ifndef DEVICE_H
#define DEVICE_H

#include <QWidget>
#include <QIcon>
namespace Ui {
class Device;
}

class Device : public QWidget
{
    Q_OBJECT

public:
    explicit Device(QWidget *parent = nullptr);
    ~Device();


private:
    Ui::Device *ui;

    QString ipAddress;
    quint16 port;
    QString deviceName;
    QImage deviceIcon;
    QString deviceType;
};

#endif // DEVICE_H
