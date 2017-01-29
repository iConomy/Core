package com.iCo6.listeners;

import com.iCo6.iConomy;
import com.iCo6.system.events.MoneyTransactionEvent;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Created by JOO200 on 28.01.2017.
 * iCo6 by Team iCo6 add by JOO200
 *
 * copyright Copyright Nexua LLC (C) 2015
 * @author Nijikokun nijikokun@gmail.com
 * @author SpaceManiac
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

public class MoneyTransactionListener implements Listener {

    private Template template;

    public MoneyTransactionListener(iConomy plugin) {
        this.template = iConomy.Template;
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onMoneyTransactionEvent(MoneyTransactionEvent e) {
        if(!e.sendMessage()) return;

        String tag = template.color(Template.Node.TAG_MONEY);

        switch(e.getState()) {
            case SELF_PAYMENT:
                template.set(Template.Node.PAYMENT_SELF);
                Messaging.send(e.getSender(), template.parse());
                break;
            case ERROR_ACCOUNT:
                template.set(Template.Node.ERROR_ACCOUNT);
                template.add("name", e.getReceiver());
                Messaging.send(e.getSender(), tag + template.parse());
                break;
            case NO_MONEY:
                template.set(Template.Node.ERROR_FUNDS);
                Messaging.send(e.getSender(), tag + template.parse());
                break;
            case COMPLETE:
                template.set(Template.Node.PAYMENT_TO);
                template.add("name", e.getReceiver());
                template.add("amount", iConomy.format(e.getMoney()));
                Messaging.send(e.getSender(), tag + template.parse());
                Player to = iConomy.Server.getPlayer(e.getReceiver());

                if(to != null) {
                    template.set(Template.Node.PAYMENT_FROM);
                    template.add("name", e.getSender().getName());
                    template.add("amount", iConomy.format(e.getMoney()));

                    Messaging.send(to, tag + template.parse());
                }
                break;
        }
    }
}
