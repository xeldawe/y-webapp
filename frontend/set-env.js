const fs = require('fs');
const targetPath = './src/environments/environment.prod.ts';
const apiKey = process.env.API_KEY || 'rsa123';  // Fallback to 'rsa123' if API_KEY is not set
const envConfigFile = `export const environment = {
  production: true,
  apiKey: '${apiKey}',
  apiUrl: 'http://34.79.119.8'
};`;

fs.writeFile(targetPath, envConfigFile, function (err) {
  if (err) {
    console.log(err);
  }
  console.log(`Output generated at ${targetPath}`);
});
