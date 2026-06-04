const BASE = '/api/lottery'

async function request(url, options = {}) {
  const res = await fetch(BASE + url, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  })
  return res.json()
}

export function getItems()           { return request('/items') }
export function addItem(item)        { return request('/items', { method: 'POST', body: JSON.stringify(item) }) }
export function removeItem(name)     { return request('/items/' + encodeURIComponent(name), { method: 'DELETE' }) }
export function clearItems()         { return request('/items', { method: 'DELETE' }) }
export function drawSingle()         { return request('/draw/single', { method: 'POST' }) }
export function drawMultiple(count)  { return request('/draw/multiple', { method: 'POST', body: JSON.stringify({ count }) }) }
export function getHistory()         { return request('/history') }
export function clearHistory()       { return request('/history', { method: 'DELETE' }) }
export async function checkHealth()  {
  try {
    const res = await fetch(BASE + '/items', { method: 'GET', signal: AbortSignal.timeout(3000) })
    return res.ok
  } catch {
    return false
  }
}
