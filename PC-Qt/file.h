#ifndef FILE_H
#define FILE_H

#include <QWidget>

namespace Ui {
class File;
}

class File : public QWidget
{
    Q_OBJECT

public:
    explicit File(QWidget *parent = nullptr);
    explicit File(QString name, QString absolute_address, QWidget *parent=nullptr);
    ~File();
    QString getFileName();
    QString getFileAddress();
private:
    Ui::File *ui;

    QIcon *Icon;
    QString name;
    QString absolute_address;
};

#endif // FILE_H
