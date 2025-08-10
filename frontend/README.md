# Ecommerce Frontend

A modern React frontend for the multi-tenant ecommerce platform built with TypeScript, Vite, Material-UI, and real-time Kafka integration.

## Features

- **Modern React 18** with TypeScript for type safety
- **Vite** for fast development and building
- **Material-UI (MUI)** for beautiful, accessible components
- **React Router** for client-side routing
- **React Query** for server state management
- **Zustand** for client state management
- **React Hook Form** for form handling
- **Real-time notifications** with Kafka/WebSocket integration
- **Notistack** for toast notifications

## Tech Stack

- React 18
- TypeScript
- Vite
- Material-UI (MUI) v5
- React Router DOM
- React Query (TanStack Query)
- Zustand
- React Hook Form
- SockJS + STOMP for WebSocket communication
- Notistack for notifications
- Date-fns

## Getting Started

### Prerequisites

- Node.js 18+ 
- npm or yarn

### Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

3. Open your browser and navigate to `http://localhost:3000`

### Building for Production

```bash
npm run build
```

The built files will be in the `dist` directory.

### Linting

```bash
npm run lint
```

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── Header.tsx
│   ├── Footer.tsx
│   ├── Layout.tsx
│   ├── ProtectedRoute.tsx
│   └── ProductCard.tsx
├── pages/              # Page components
│   ├── Home.tsx
│   ├── Login.tsx
│   ├── Register.tsx
│   ├── Products.tsx
│   ├── ProductDetail.tsx
│   ├── Cart.tsx
│   ├── Dashboard.tsx
│   ├── Orders.tsx
│   ├── Profile.tsx
│   └── admin/          # Admin pages
│       ├── AdminDashboard.tsx
│       └── TenantManagement.tsx
├── services/           # API services
│   ├── api.ts
│   ├── authService.ts
│   ├── productService.ts
│   └── orderService.ts
├── stores/             # State management
│   ├── authStore.ts
│   └── cartStore.ts
├── App.tsx             # Main app component
├── main.tsx           # Entry point
└── index.css          # Global styles
```

## Key Features

### Authentication
- JWT-based authentication
- Protected routes
- Role-based access control
- User registration and login

### Shopping Experience
- Product browsing with filters
- Product search
- Shopping cart management
- Order placement and tracking

### User Management
- User profiles
- Order history
- Account settings
- Password management

### Real-time Features
- **Live notifications** via Kafka/WebSocket integration
- **Real-time order updates** for customers
- **Live product updates** for tenants
- **Admin notifications** for system events
- **Toast notifications** with Notistack

### Admin Features
- System overview dashboard
- Tenant management
- User management
- Analytics and reporting
- Real-time system monitoring

### Multi-tenant Support
- Tenant-specific product catalogs
- Role-based access (Super Admin, Tenant, User)
- Tenant approval workflow
- Real-time tenant notifications

## API Integration

The frontend communicates with the backend microservices through REST APIs:

- **Auth Service**: Authentication and user management
- **Product Service**: Product catalog management
- **Order Service**: Order processing and management
- **Tenant Service**: Tenant management

## Environment Variables

Create a `.env` file in the frontend directory:

```env
VITE_API_BASE_URL=http://localhost:8080
```

## Development

### Adding New Pages

1. Create a new component in `src/pages/`
2. Add the route in `src/App.tsx`
3. Update navigation if needed

### Adding New Components

1. Create a new component in `src/components/`
2. Export it as default
3. Import and use in pages

### Adding New Services

1. Create a new service file in `src/services/`
2. Define TypeScript interfaces
3. Implement API calls using axios

## Contributing

1. Follow the existing code structure
2. Use TypeScript for all new code
3. Follow the established naming conventions
4. Add proper error handling
5. Test your changes thoroughly

## License

This project is licensed under the MIT License.
