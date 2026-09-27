export default function EmptyState({ icon = '📭', title = 'Nothing here yet', description }) {
  return (
    <div className="empty-state">
      <div className="icon">{icon}</div>
      <div style={{ fontWeight: 600, color: '#16241f' }}>{title}</div>
      {description && <div style={{ marginTop: 4 }}>{description}</div>}
    </div>
  );
}
