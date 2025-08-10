import SockJS from 'sockjs-client';
import { Stomp, Client } from 'stompjs';
import config from '../config/environment';

// Define message types for type safety
export interface KafkaMessage {
  id: string;
  type: 'ORDER_CREATED' | 'ORDER_UPDATED' | 'PRODUCT_UPDATED' | 'TENANT_APPROVED' | 'NOTIFICATION';
  payload: any;
  timestamp: string;
  tenantId?: string;
  userId?: string;
}

export interface NotificationMessage {
  id: string;
  title: string;
  message: string;
  type: 'info' | 'success' | 'warning' | 'error';
  timestamp: string;
  userId?: string;
  tenantId?: string;
}

export interface OrderEvent {
  orderId: string;
  status: 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  customerId: string;
  tenantId: string;
  total: number;
  timestamp: string;
}

export interface ProductEvent {
  productId: string;
  action: 'CREATED' | 'UPDATED' | 'DELETED';
  tenantId: string;
  productName: string;
  timestamp: string;
}

export interface TenantEvent {
  tenantId: string;
  action: 'APPROVED' | 'REJECTED' | 'SUSPENDED';
  tenantName: string;
  timestamp: string;
}

class KafkaService {
  private client: Client | null = null;
  private connected = false;
  private subscriptions: Map<string, any> = new Map();
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectInterval = 5000;

  constructor() {
    this.connect();
  }

  // Connect to WebSocket broker (Spring Boot with STOMP)
  connect(): void {
    const socket = new SockJS(config.websocketUrl);
    this.client = Stomp.over(socket);

    // Disable debug logging in production
    if (import.meta.env.PROD) {
      this.client.debug = () => {};
    }

    this.client.connect(
      {},
      (frame) => {
        console.log('Connected to WebSocket broker:', frame);
        this.connected = true;
        this.reconnectAttempts = 0;
      },
      (error) => {
        console.error('WebSocket connection error:', error);
        this.connected = false;
        this.handleReconnect();
      }
    );
  }

  // Handle reconnection logic
  private handleReconnect(): void {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      setTimeout(() => {
        this.reconnectAttempts++;
        console.log(`Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
        this.connect();
      }, this.reconnectInterval);
    } else {
      console.error('Max reconnection attempts reached. Please refresh the page.');
    }
  }

  // Subscribe to order events
  subscribeToOrderEvents(
    userId: string,
    onMessage: (message: OrderEvent) => void
  ): () => void {
    if (!this.client || !this.connected) {
      console.warn('WebSocket not connected. Cannot subscribe to order events.');
      return () => {};
    }

    const subscription = this.client.subscribe(
      `/user/${userId}/queue/orders`,
      (message) => {
        try {
          const orderEvent: OrderEvent = JSON.parse(message.body);
          onMessage(orderEvent);
        } catch (error) {
          console.error('Error parsing order event:', error);
        }
      }
    );

    const subscriptionId = `orders-${userId}`;
    this.subscriptions.set(subscriptionId, subscription);

    return () => {
      subscription.unsubscribe();
      this.subscriptions.delete(subscriptionId);
    };
  }

  // Subscribe to product events for a tenant
  subscribeToProductEvents(
    tenantId: string,
    onMessage: (message: ProductEvent) => void
  ): () => void {
    if (!this.client || !this.connected) {
      console.warn('WebSocket not connected. Cannot subscribe to product events.');
      return () => {};
    }

    const subscription = this.client.subscribe(
      `/topic/tenant/${tenantId}/products`,
      (message) => {
        try {
          const productEvent: ProductEvent = JSON.parse(message.body);
          onMessage(productEvent);
        } catch (error) {
          console.error('Error parsing product event:', error);
        }
      }
    );

    const subscriptionId = `products-${tenantId}`;
    this.subscriptions.set(subscriptionId, subscription);

    return () => {
      subscription.unsubscribe();
      this.subscriptions.delete(subscriptionId);
    };
  }

  // Subscribe to tenant events (for admin)
  subscribeToTenantEvents(
    onMessage: (message: TenantEvent) => void
  ): () => void {
    if (!this.client || !this.connected) {
      console.warn('WebSocket not connected. Cannot subscribe to tenant events.');
      return () => {};
    }

    const subscription = this.client.subscribe(
      '/topic/admin/tenants',
      (message) => {
        try {
          const tenantEvent: TenantEvent = JSON.parse(message.body);
          onMessage(tenantEvent);
        } catch (error) {
          console.error('Error parsing tenant event:', error);
        }
      }
    );

    const subscriptionId = 'tenants-admin';
    this.subscriptions.set(subscriptionId, subscription);

    return () => {
      subscription.unsubscribe();
      this.subscriptions.delete(subscriptionId);
    };
  }

  // Subscribe to notifications for a user
  subscribeToNotifications(
    userId: string,
    onMessage: (message: NotificationMessage) => void
  ): () => void {
    if (!this.client || !this.connected) {
      console.warn('WebSocket not connected. Cannot subscribe to notifications.');
      return () => {};
    }

    const subscription = this.client.subscribe(
      `/user/${userId}/queue/notifications`,
      (message) => {
        try {
          const notification: NotificationMessage = JSON.parse(message.body);
          onMessage(notification);
        } catch (error) {
          console.error('Error parsing notification:', error);
        }
      }
    );

    const subscriptionId = `notifications-${userId}`;
    this.subscriptions.set(subscriptionId, subscription);

    return () => {
      subscription.unsubscribe();
      this.subscriptions.delete(subscriptionId);
    };
  }

  // Send a message to a topic
  sendMessage(destination: string, message: any): void {
    if (!this.client || !this.connected) {
      console.warn('WebSocket not connected. Cannot send message.');
      return;
    }

    this.client.send(destination, {}, JSON.stringify(message));
  }

  // Publish order event
  publishOrderEvent(orderEvent: OrderEvent): void {
    this.sendMessage('/app/orders', orderEvent);
  }

  // Publish product event
  publishProductEvent(productEvent: ProductEvent): void {
    this.sendMessage('/app/products', productEvent);
  }

  // Publish notification
  publishNotification(notification: NotificationMessage): void {
    this.sendMessage('/app/notifications', notification);
  }

  // Disconnect from WebSocket
  disconnect(): void {
    if (this.client) {
      // Unsubscribe from all subscriptions
      this.subscriptions.forEach((subscription) => {
        subscription.unsubscribe();
      });
      this.subscriptions.clear();

      // Disconnect the client
      this.client.disconnect(() => {
        console.log('Disconnected from WebSocket broker');
        this.connected = false;
      });
    }
  }

  // Check if connected
  isConnected(): boolean {
    return this.connected;
  }

  // Get connection status
  getConnectionStatus(): 'connected' | 'disconnected' | 'connecting' {
    if (this.connected) return 'connected';
    if (this.reconnectAttempts > 0 && this.reconnectAttempts < this.maxReconnectAttempts) {
      return 'connecting';
    }
    return 'disconnected';
  }
}

// Create a singleton instance
export const kafkaService = new KafkaService();

export default kafkaService;


