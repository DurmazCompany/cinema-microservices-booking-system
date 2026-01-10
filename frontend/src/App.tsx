import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import Login from './pages/Login';
import Showtimes from './pages/Showtimes';
import Seats from './pages/Seats';
import MyBookings from './pages/MyBookings';
import Status from './pages/Status';

function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/showtimes" element={<Showtimes />} />
          <Route path="/showtimes/:showtimeId/seats" element={<Seats />} />
          <Route path="/my-bookings" element={<MyBookings />} />
          <Route path="/status" element={<Status />} />

          {/* Default Route */}
          <Route path="/" element={<Navigate to="/showtimes" replace />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}

export default App;
