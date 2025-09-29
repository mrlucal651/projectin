import React from 'react';
import { Car, Truck, Clock, Zap, TrendingUp, MapPin, Fuel, Users } from 'lucide-react';
import MaintenancePieChart from './MaintenancePieChart';

const FleetOverview: React.FC = () => {
  const stats = [
    { label: 'Active Vehicles', value: '247', icon: Car, change: '+12%', trend: 'up' },
    { label: 'Total Distance', value: '73,742 km', icon: MapPin, change: '+8%', trend: 'up' },
    { label: 'Fuel Efficiency', value: '92%', icon: Fuel, change: '+5%', trend: 'up' },
    { label: 'Active Drivers', value: '89', icon: Users, change: '-2%', trend: 'down' },
  ];

  const vehicles = [
    { id: 'FL-001', type: 'Delivery Van', status: 'En Route', location: 'Connaught Place', battery: 85, driver: 'Rajesh Kumar' },
    { id: 'FL-002', type: 'Cargo Truck', status: 'Loading', location: 'Gurgaon Hub', battery: 92, driver: 'Priya Sharma' },
    { id: 'FL-003', type: 'Pickup Truck', status: 'Available', location: 'Noida Depot', battery: 100, driver: 'Amit Singh' },
    { id: 'FL-004', type: 'Delivery Van', status: 'En Route', location: 'Bangalore Tech Park', battery: 67, driver: 'Sneha Patel' },
    { id: 'FL-005', type: 'Cargo Truck', status: 'Maintenance', location: 'Mumbai Service Center', battery: 0, driver: 'Vikram Reddy' },
  ];

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'En Route': return 'bg-green-100 text-green-800';
      case 'Loading': return 'bg-yellow-100 text-yellow-800';
      case 'Available': return 'bg-blue-100 text-blue-800';
      case 'Maintenance': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {stats.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <div key={index} className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
              <div className="flex items-center justify-between mb-4">
                <div className="bg-gradient-to-r from-purple-100 to-teal-100 p-3 rounded-xl">
                  <Icon className="w-6 h-6 text-purple-600" />
                </div>
                <div className={`flex items-center text-sm font-medium ${
                  stat.trend === 'up' ? 'text-green-600' : 'text-red-600'
                }`}>
                  <TrendingUp className={`w-4 h-4 mr-1 ${
                    stat.trend === 'down' ? 'rotate-180' : ''
                  }`} />
                  {stat.change}
                </div>
              </div>
              <div className="text-2xl font-bold text-gray-900 mb-1">{stat.value}</div>
              <div className="text-sm text-gray-600">{stat.label}</div>
            </div>
          );
        })}
      </div>

      {/* AI Insights */}
      <div className="bg-gradient-to-r from-purple-50 to-teal-50 rounded-2xl p-6 mb-8 border border-purple-100">
        <div className="flex items-center mb-4">
          <div className="bg-gradient-to-r from-purple-500 to-teal-500 p-2 rounded-lg">
            <Zap className="w-5 h-5 text-white" />
          </div>
          <h3 className="text-lg font-semibold text-gray-900 ml-3">AI Fleet Insights</h3>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-white/70 rounded-xl p-4">
            <h4 className="font-medium text-gray-900 mb-2">Route Optimization</h4>
            <p className="text-sm text-gray-600">AI suggests 15% shorter routes for today's deliveries</p>
          </div>
          <div className="bg-white/70 rounded-xl p-4">
            <h4 className="font-medium text-gray-900 mb-2">Maintenance Alert</h4>
            <p className="text-sm text-gray-600">3 vehicles due for scheduled maintenance this week</p>
          </div>
          <div className="bg-white/70 rounded-xl p-4">
            <h4 className="font-medium text-gray-900 mb-2">Fuel Efficiency</h4>
            <p className="text-sm text-gray-600">Average fleet efficiency up 8% this month</p>
          </div>
        </div>
      </div>

      {/* Maintenance Status Chart */}
      <div className="mb-8">
        <MaintenancePieChart />
      </div>

      {/* Vehicle List */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100">
        <div className="px-6 py-4 border-b border-gray-100">
          <h3 className="text-lg font-semibold text-gray-900">Active Fleet Status</h3>
        </div>
        <div className="divide-y divide-gray-100">
          {vehicles.map((vehicle) => (
            <div key={vehicle.id} className="px-6 py-4 hover:bg-gray-50 transition-colors">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-4">
                  <div className="bg-gradient-to-r from-gray-100 to-gray-200 p-3 rounded-xl">
                    {vehicle.type.includes('Truck') ? (
                      <Truck className="w-5 h-5 text-gray-600" />
                    ) : (
                      <Car className="w-5 h-5 text-gray-600" />
                    )}
                  </div>
                  <div>
                    <div className="font-medium text-gray-900">{vehicle.id}</div>
                    <div className="text-sm text-gray-600">{vehicle.type}</div>
                  </div>
                </div>
                
                <div className="flex items-center space-x-6">
                  <div className="text-center">
                    <div className="text-sm font-medium text-gray-900">{vehicle.driver}</div>
                    <div className="text-xs text-gray-600">Driver</div>
                  </div>
                  
                  <div className="text-center">
                    <div className="text-sm font-medium text-gray-900">{vehicle.location}</div>
                    <div className="text-xs text-gray-600">Location</div>
                  </div>
                  
                  <div className="text-center">
                    <div className="text-sm font-medium text-gray-900">{vehicle.battery}%</div>
                    <div className="text-xs text-gray-600">Battery</div>
                  </div>
                  
                  <span className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(vehicle.status)}`}>
                    {vehicle.status}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default FleetOverview;