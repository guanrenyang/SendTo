# SendTo

Project of **SJTU-CS339 Computer Networks**.

## Introduction

SendTo is an end-to-end file transfer application, which supports device detection by UDP and Bluetooth and file transferring by TCP.

SendTo 是一款端到端的文件传输应用。它支持局域网设备感知（UDP协议）、近距离设备感知（蓝牙），并可以传输任意大小的文件（TCP协议）。

## Features

* Multiple device perception methods
* Transfer files of any size

## Build & Run

If you just want to use it, feel free to skip to **Usage **section.

* IDE: Qt Creator 4.15.0(Community)
* Kits: Desktop Qt 5.12.11 MSVC 2015 64bit (MSVC is necessary. Without MSVC Bluetooth is not supported)
* Operating System: Windows 10, 11

## Usage

The executable file is in [Executable](https://github.com/guanrenyang/SendTo/tree/master/Executable) folder. You just need to double click on the `SendTo.exe` file to use it. All runtime dependencies have been packaged in the folder.

### Device Selection

SendTo provides three ways to specify a device:

* In LAN (by UPD broadcasting)
* Nearby (by Bluetooth)
* Specify an IP address

#### In LAN

In LAN mode could detect devices in the same local area network.

Click the "局域网" button(the 1st in the yellow rectangle) to switch to **In LAN** mode. Click the "重新扫描" button(the one in red rectangle) to do a scan. After that, select a device in the drop down box(upper in the red rectangle).

![](https://s3.bmp.ovh/imgs/2022/01/a4291827651e440d.png)

#### Nearby

**Nearby** mode could detect devices around, which works in the similar way as **In LAN** mode.

Click the "蓝牙" button(the 2nd in the yellow rectangle) to switch to **Nearby** mode. Click the "重新扫描" button(the one in red rectangle) to do a scan. After that, select a device in the new window.

![](https://s3.bmp.ovh/imgs/2022/01/0b3ffbe22485c33a.png)

#### Specify an IP address

SendTo allows you to specify an IP address to send to.

Click the "指定接收者" button (the 3rd in the yellow square) and enter the receiver's IP address in the corresponding editable text box. 

The local IP address is also displayed on this interface. If you are the receiver, you can tell it to the sender.

![](https://s3.bmp.ovh/imgs/2022/01/8b96d1c5d7f8292e.png)

### Files Selection

* Drag and drop: SendTo provides a drag-and-drop interface for selecting files, just drag files and drop them on the box in the lower half of the window(in big red rectangle)
* Traditional way: Click on the "发送文件" button (in small red rectangle) and Windows system will pop up a file selection window.
* Double click delete: The file to be sent will be displayed in the box in the lower half of the window, double-click the file name to delete the file from the list

![](https://s3.bmp.ovh/imgs/2022/01/e2836d83852b7520.png)

### Send Files

Just click on "开始发送" button(the yellow rectangle in the lower right corner), sending will start and a progress bar will show the progress.

## Contact Me

My e-mail address is [guanrenyang@qq.com](mailto:guanrenyang@qq.com).

If you have any problems of this project, just send an e-mail to me. I will take every email seriously.

