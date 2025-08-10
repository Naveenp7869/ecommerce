import { useQuery } from '@tanstack/react-query'
import { 
  UsersIcon, 
  ShoppingBagIcon, 
  CurrencyDollarIcon, 
  ChartBarIcon,
  BuildingStorefrontIcon
} from '@heroicons/react/24/outline'

const AdminDashboard = () => {
  // Mock data for admin dashboard
  const stats = [
    {
      name: 'Total Users',
      value: '1,234',
      change: '+12%',
      changeType: 'positive',
      icon: UsersIcon,
    },
    {
      name: 'Total Orders',
      value: '5,678',
      change: '+8%',
      changeType: 'positive',
      icon: ShoppingBagIcon,
    },
    {
      name: 'Revenue',
      value: '$123,456',
      change: '+15%',
      changeType: 'positive',
      icon: CurrencyDollarIcon,
    },
    {
      name: 'Active Tenants',
      value: '89',
      change: '+3%',
      changeType: 'positive',
      icon: BuildingStorefrontIcon,
    },
  ]

  const recentActivities = [
    {
      id: 1,
      type: 'user_registration',
      message: 'New user registered: john.doe@example.com',
      timestamp: '2 minutes ago',
    },
    {
      id: 2,
      type: 'order_placed',
      message: 'Order #12345 placed by tenant: TechStore',
      timestamp: '5 minutes ago',
    },
    {
      id: 3,
      type: 'tenant_approved',
      message: 'Tenant "FashionHub" approved for registration',
      timestamp: '10 minutes ago',
    },
    {
      id: 4,
      type: 'payment_received',
      message: 'Payment received: $1,234.56 from FashionHub',
      timestamp: '15 minutes ago',
    },
  ]

  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Admin Dashboard</h1>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {stats.map((stat) => (
          <div key={stat.name} className="bg-white rounded-lg border p-6">
            <div className="flex items-center">
              <div className="bg-primary-100 p-3 rounded-lg">
                <stat.icon className="h-6 w-6 text-primary-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">{stat.name}</p>
                <p className="text-2xl font-semibold text-gray-900">{stat.value}</p>
                <p className={`text-sm ${stat.changeType === 'positive' ? 'text-green-600' : 'text-red-600'}`}>
                  {stat.change} from last month
                </p>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
        <div className="bg-white rounded-lg border">
          <div className="p-6 border-b">
            <h2 className="text-lg font-semibold text-gray-900">Quick Actions</h2>
          </div>
          <div className="p-6">
            <div className="space-y-4">
              <button className="w-full text-left p-4 rounded-lg border hover:bg-gray-50 transition-colors">
                <div className="flex items-center">
                  <UsersIcon className="h-5 w-5 text-gray-400 mr-3" />
                  <div>
                    <p className="font-medium text-gray-900">Manage Users</p>
                    <p className="text-sm text-gray-600">View and manage all users</p>
                  </div>
                </div>
              </button>
              <button className="w-full text-left p-4 rounded-lg border hover:bg-gray-50 transition-colors">
                <div className="flex items-center">
                  <BuildingStorefrontIcon className="h-5 w-5 text-gray-400 mr-3" />
                  <div>
                    <p className="font-medium text-gray-900">Manage Tenants</p>
                    <p className="text-sm text-gray-600">Approve and manage tenants</p>
                  </div>
                </div>
              </button>
              <button className="w-full text-left p-4 rounded-lg border hover:bg-gray-50 transition-colors">
                <div className="flex items-center">
                  <ShoppingBagIcon className="h-5 w-5 text-gray-400 mr-3" />
                  <div>
                    <p className="font-medium text-gray-900">View Orders</p>
                    <p className="text-sm text-gray-600">Monitor all system orders</p>
                  </div>
                </div>
              </button>
              <button className="w-full text-left p-4 rounded-lg border hover:bg-gray-50 transition-colors">
                <div className="flex items-center">
                  <ChartBarIcon className="h-5 w-5 text-gray-400 mr-3" />
                  <div>
                    <p className="font-medium text-gray-900">Analytics</p>
                    <p className="text-sm text-gray-600">View system analytics</p>
                  </div>
                </div>
              </button>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg border">
          <div className="p-6 border-b">
            <h2 className="text-lg font-semibold text-gray-900">Recent Activity</h2>
          </div>
          <div className="p-6">
            <div className="space-y-4">
              {recentActivities.map((activity) => (
                <div key={activity.id} className="flex items-start space-x-3">
                  <div className="flex-shrink-0 w-2 h-2 bg-primary-600 rounded-full mt-2"></div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm text-gray-900">{activity.message}</p>
                    <p className="text-xs text-gray-500">{activity.timestamp}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* System Overview */}
      <div className="bg-white rounded-lg border">
        <div className="p-6 border-b">
          <h2 className="text-lg font-semibold text-gray-900">System Overview</h2>
        </div>
        <div className="p-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div>
              <h3 className="font-medium text-gray-900 mb-2">System Health</h3>
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span className="text-sm text-gray-600">Database</span>
                  <span className="text-sm font-medium text-green-600">Healthy</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm text-gray-600">API Services</span>
                  <span className="text-sm font-medium text-green-600">All Online</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm text-gray-600">Storage</span>
                  <span className="text-sm font-medium text-yellow-600">75% Used</span>
                </div>
              </div>
            </div>
            <div>
              <h3 className="font-medium text-gray-900 mb-2">Performance</h3>
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span className="text-sm text-gray-600">Response Time</span>
                  <span className="text-sm font-medium text-green-600">120ms</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm text-gray-600">Uptime</span>
                  <span className="text-sm font-medium text-green-600">99.9%</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm text-gray-600">Active Sessions</span>
                  <span className="text-sm font-medium text-blue-600">1,234</span>
                </div>
              </div>
            </div>
            <div>
              <h3 className="font-medium text-gray-900 mb-2">Security</h3>
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span className="text-sm text-gray-600">Last Scan</span>
                  <span className="text-sm font-medium text-green-600">2 hours ago</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm text-gray-600">Threats</span>
                  <span className="text-sm font-medium text-green-600">0</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-sm text-gray-600">SSL Status</span>
                  <span className="text-sm font-medium text-green-600">Valid</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default AdminDashboard
