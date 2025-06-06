﻿#ifndef _ROOM_BUTTON_H
#define _ROOM_BUTTON_H

#include <QWidget>

class Engine;
class QPushButton;
class RoomButton : public QWidget
{
    Q_OBJECT

public:
    RoomButton(QWidget* parent = nullptr);
    ~RoomButton();

private:
    void setUi();

Q_SIGNALS:
    void sigNertcBeautySetting();

private Q_SLOTS:
    void onNertcBeautySettingClicked();

private:

    QPushButton* nertc_beauty_setting_btn_;
};



#endif //_ROOM_BUTTON_H
