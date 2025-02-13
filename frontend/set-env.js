const fs = require('fs');
const targetPath = './src/environments/environment.prod.ts';
const apiKey = process.env.API_KEY || 'rsa123';  // Fallback to 'rsa123' if API_KEY is not set
const filterInterval = process.env.FILTER_INTERVAL || '1m';  // Fallback to '1m' if FILTER_INTERVAL is not set
const apiUrl = process.env.API_URL || 'http://localhost';  // Fallback to 'http://localhost' if API_URL is not set
const envConfigFile = `export const environment = {
  production: true,
  apiKey: '${apiKey}',
  apiUrl: '${apiUrl}',
  filterInterval: '${filterInterval}'
};`;

fs.writeFile(targetPath, envConfigFile, function (err) {
  if (err) {
    console.log(err);
  }
  console.log(`Output generated at ${targetPath}`);
});
