import Sidebar from './Sidebar';
import Navbar from './Navbar';

export default function Layout({ title, children }) {
  return (
    <div className="app-shell">
      <Sidebar />
      <div className="main-area">
        <Navbar title={title} />
        <div className="page-content">{children}</div>
      </div>
    </div>
  );
}
