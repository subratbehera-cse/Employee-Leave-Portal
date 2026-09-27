export default function Loading({ label = 'Loading…' }) {
  return (
    <div className="loading-state">
      <div className="spinner" />
      <div>{label}</div>
    </div>
  );
}
