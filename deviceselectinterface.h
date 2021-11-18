#ifndef DEVICESELECTINTERFACE_H
#define DEVICESELECTINTERFACE_H

#include <QWidget>
#include <QPushButton>
namespace Ui {
class DeviceSelectInterface;
}

class DeviceSelectInterface : public QWidget
{
    Q_OBJECT

public:
    explicit DeviceSelectInterface(QWidget *parent = nullptr);
    ~DeviceSelectInterface();
 private:
    Ui::DeviceSelectInterface *ui;
};

#endif // DEVICESELECTINTERFACE_H
