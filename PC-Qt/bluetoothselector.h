#ifndef BLUETOOTHSLECTOR_H
#define BLUETOOTHSLECTOR_H

#include <QtWidgets/qdialog.h>

#include <QtBluetooth/qbluetoothaddress.h>
#include <QtBluetooth/qbluetoothserviceinfo.h>
#include <QtBluetooth/qbluetoothuuid.h>
#include <QThread>
QT_FORWARD_DECLARE_CLASS(QBluetoothServiceDiscoveryAgent)
QT_FORWARD_DECLARE_CLASS(QListWidgetItem)

QT_USE_NAMESPACE

QT_BEGIN_NAMESPACE
namespace Ui {
    class BluetoothSelector;
}
QT_END_NAMESPACE

class BluetoothSelector : public QDialog
{
    Q_OBJECT

public:
    explicit BluetoothSelector(const QBluetoothAddress &localAdapter, QWidget *parent = nullptr);
    ~BluetoothSelector();

    void startDiscovery(const QBluetoothUuid &uuid);
    void stopDiscovery();
    QBluetoothServiceInfo service() const;

private:
    Ui::BluetoothSelector *ui;

    QBluetoothServiceDiscoveryAgent *m_discoveryAgent;
    QBluetoothServiceInfo m_service;
    QMap<QListWidgetItem *, QBluetoothServiceInfo> m_discoveredServices;

private slots:
    void serviceDiscovered(const QBluetoothServiceInfo &serviceInfo);
    void discoveryFinished();
    void on_remoteDevices_itemActivated(QListWidgetItem *item);
    void on_cancelButton_clicked();
};

#endif // REMOTESELECTOR_H
