import React, { useState, useEffect } from 'react';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend } from 'recharts';
import { Wrench, AlertTriangle, CheckCircle } from 'lucide-react';

interface MaintenanceData {
  healthy: number;
  due: number;
  critical: number;
}

interface ChartData {
  name: string;
  value: number;
  color: string;
  icon: React.ComponentType<any>;
}

const MaintenancePieChart: React.FC = () => {
  const [data, setData] = useState<MaintenanceData>({ healthy: 0, due: 0, critical: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchMaintenanceStatus = async () => {
    try {
      setLoading(true);
      const response = await fetch('/api/vehicles/maintenance-status');
      if (!response.ok) {
        throw new Error('Failed to fetch maintenance status');
      }
      const maintenanceData = await response.json();
      setData(maintenanceData);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
      console.error('Error fetching maintenance status:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMaintenanceStatus();
    
    // Set up auto-refresh every 30 seconds
    const interval = setInterval(fetchMaintenanceStatus, 30000);
    
    return () => clearInterval(interval);
  }, []);

  const chartData: ChartData[] = [
    {
      name: 'Healthy',
      value: data.healthy,
      color: '#4CAF50',
      icon: CheckCircle
    },
    {
      name: 'Due',
      value: data.due,
      color: '#FFC107',
      icon: AlertTriangle
    },
    {
      name: 'Critical',
      value: data.critical,
      color: '#F44336',
      icon: Wrench
    }
  ];

  const total = data.healthy + data.due + data.critical;

  const CustomTooltip = ({ active, payload }: any) => {
    if (active && payload && payload.length) {
      const data = payload[0];
      const percentage = total > 0 ? ((data.value / total) * 100).toFixed(1) : '0';
      
      return (
        <div className="bg-white p-3 rounded-lg shadow-lg border border-gray-200">
          <div className="flex items-center space-x-2 mb-1">
            <div 
              className="w-3 h-3 rounded-full" 
              style={{ backgroundColor: data.payload.color }}
            ></div>
            <span className="font-medium text-gray-900">{data.payload.name}</span>
          </div>
          <div className="text-sm text-gray-600">
            <div>Vehicles: {data.value}</div>
            <div>Percentage: {percentage}%</div>
          </div>
        </div>
      );
    }
    return null;
  };

  const CustomLegend = ({ payload }: any) => {
    return (
      <div className="flex justify-center space-x-6 mt-4">
        {payload.map((entry: any, index: number) => {
          const IconComponent = chartData[index]?.icon;
          return (
            <div key={entry.value} className="flex items-center space-x-2">
              {IconComponent && <IconComponent className="w-4 h-4" style={{ color: entry.color }} />}
              <span className="text-sm font-medium text-gray-700">{entry.value}</span>
              <span className="text-sm text-gray-500">({chartData[index]?.value})</span>
            </div>
          );
        })}
      </div>
    );
  };

  if (loading) {
    return (
      <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-lg font-semibold text-gray-900">Maintenance Status</h3>
          <div className="flex items-center space-x-2">
            <div className="w-2 h-2 bg-blue-500 rounded-full animate-pulse"></div>
            <span className="text-sm text-gray-600">Loading...</span>
          </div>
        </div>
        <div className="h-80 flex items-center justify-center">
          <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-lg font-semibold text-gray-900">Maintenance Status</h3>
          <button 
            onClick={fetchMaintenanceStatus}
            className="text-sm text-blue-600 hover:text-blue-800 font-medium"
          >
            Retry
          </button>
        </div>
        <div className="h-80 flex items-center justify-center">
          <div className="text-center">
            <AlertTriangle className="w-12 h-12 text-red-500 mx-auto mb-4" />
            <p className="text-gray-600 mb-2">Failed to load maintenance data</p>
            <p className="text-sm text-gray-500">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center space-x-3">
          <div className="bg-gradient-to-r from-purple-100 to-teal-100 p-3 rounded-xl">
            <Wrench className="w-6 h-6 text-purple-600" />
          </div>
          <div>
            <h3 className="text-lg font-semibold text-gray-900">Maintenance Status</h3>
            <p className="text-sm text-gray-600">Fleet health overview</p>
          </div>
        </div>
        <div className="flex items-center space-x-2">
          <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
          <span className="text-sm text-gray-600">Live</span>
        </div>
      </div>

      {total === 0 ? (
        <div className="h-80 flex items-center justify-center">
          <div className="text-center">
            <Wrench className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">No vehicle data available</p>
          </div>
        </div>
      ) : (
        <>
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={chartData}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={120}
                  paddingAngle={2}
                  dataKey="value"
                >
                  {chartData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip content={<CustomTooltip />} />
                <Legend content={<CustomLegend />} />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {/* Summary Stats */}
          <div className="mt-6 grid grid-cols-3 gap-4">
            <div className="text-center p-4 bg-green-50 rounded-xl">
              <div className="flex items-center justify-center mb-2">
                <CheckCircle className="w-5 h-5 text-green-600 mr-2" />
                <span className="text-2xl font-bold text-green-600">{data.healthy}</span>
              </div>
              <div className="text-sm text-gray-600">Healthy Vehicles</div>
              <div className="text-xs text-green-600 mt-1">
                {total > 0 ? `${((data.healthy / total) * 100).toFixed(1)}%` : '0%'}
              </div>
            </div>

            <div className="text-center p-4 bg-yellow-50 rounded-xl">
              <div className="flex items-center justify-center mb-2">
                <AlertTriangle className="w-5 h-5 text-yellow-600 mr-2" />
                <span className="text-2xl font-bold text-yellow-600">{data.due}</span>
              </div>
              <div className="text-sm text-gray-600">Maintenance Due</div>
              <div className="text-xs text-yellow-600 mt-1">
                {total > 0 ? `${((data.due / total) * 100).toFixed(1)}%` : '0%'}
              </div>
            </div>

            <div className="text-center p-4 bg-red-50 rounded-xl">
              <div className="flex items-center justify-center mb-2">
                <Wrench className="w-5 h-5 text-red-600 mr-2" />
                <span className="text-2xl font-bold text-red-600">{data.critical}</span>
              </div>
              <div className="text-sm text-gray-600">Critical Issues</div>
              <div className="text-xs text-red-600 mt-1">
                {total > 0 ? `${((data.critical / total) * 100).toFixed(1)}%` : '0%'}
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default MaintenancePieChart;