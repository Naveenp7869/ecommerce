import { create } from 'zustand';
import { NotificationMessage } from '../services/kafkaService';

export interface Notification extends NotificationMessage {
  read: boolean;
}

interface NotificationState {
  notifications: Notification[];
  unreadCount: number;
  addNotification: (notification: NotificationMessage) => void;
  markAsRead: (notificationId: string) => void;
  markAllAsRead: () => void;
  removeNotification: (notificationId: string) => void;
  clearAll: () => void;
  getUnreadNotifications: () => Notification[];
}

export const useNotificationStore = create<NotificationState>((set, get) => ({
  notifications: [],
  unreadCount: 0,

  addNotification: (notification: NotificationMessage) => {
    const newNotification: Notification = {
      ...notification,
      read: false,
    };

    set((state) => ({
      notifications: [newNotification, ...state.notifications].slice(0, 50), // Keep only last 50 notifications
      unreadCount: state.unreadCount + 1,
    }));
  },

  markAsRead: (notificationId: string) => {
    set((state) => {
      const updatedNotifications = state.notifications.map((notification) =>
        notification.id === notificationId && !notification.read
          ? { ...notification, read: true }
          : notification
      );

      const unreadCount = updatedNotifications.filter((n) => !n.read).length;

      return {
        notifications: updatedNotifications,
        unreadCount,
      };
    });
  },

  markAllAsRead: () => {
    set((state) => ({
      notifications: state.notifications.map((notification) => ({
        ...notification,
        read: true,
      })),
      unreadCount: 0,
    }));
  },

  removeNotification: (notificationId: string) => {
    set((state) => {
      const notificationToRemove = state.notifications.find((n) => n.id === notificationId);
      const updatedNotifications = state.notifications.filter((n) => n.id !== notificationId);
      const unreadCount = notificationToRemove && !notificationToRemove.read 
        ? state.unreadCount - 1 
        : state.unreadCount;

      return {
        notifications: updatedNotifications,
        unreadCount: Math.max(0, unreadCount),
      };
    });
  },

  clearAll: () => {
    set({
      notifications: [],
      unreadCount: 0,
    });
  },

  getUnreadNotifications: () => {
    return get().notifications.filter((notification) => !notification.read);
  },
}));


