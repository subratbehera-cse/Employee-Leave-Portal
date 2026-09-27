import EmptyState from './EmptyState';

export default function DataTable({ columns, rows, emptyTitle = 'No records found', emptyDescription, renderRow }) {
  if (!rows || rows.length === 0) {
    return <EmptyState icon="🗂️" title={emptyTitle} description={emptyDescription} />;
  }

  return (
    <div style={{ overflowX: 'auto' }}>
      <table className="data-table">
        <thead>
          <tr>
            {columns.map((col) => (
              <th key={col}>{col}</th>
            ))}
          </tr>
        </thead>
        <tbody>{rows.map((row, idx) => renderRow(row, idx))}</tbody>
      </table>
    </div>
  );
}
