console.log("Initializing pagopaBackoffice DB");
const brokerIbans = require('./usr/local/data/brokerIbans.json');
const brokerInstitutions = require('./usr/local/data/brokerInstitutions.json');
const creditorInstitutionIbans = require('./usr/local/data/creditorInstitutionIbans.json');
const maintenance = require('./usr/local/data/maintenance.json');
const pspLegacy = require('./usr/local/data/pspLegacy.json');
const tavoloOp = require('./usr/local/data/tavoloOp.json');
const taxonomies = require('./usr/local/data/taxonomies.json');
const taxonomy_groups = require('./usr/local/data/taxonomy_groups.json');
const wrappers = require('./usr/local/data/wrappers.json');

const mongoConnection = new Mongo();
const db = mongoConnection.getDB("pagopaBackoffice");

//add here collection initialization putting documents
db.getCollection('brokerIbans').insertMany(brokerIbans);
db.getCollection('brokerInstitutions').insertMany(brokerInstitutions);
db.getCollection('creditorInstitutionIbans').insertMany(creditorInstitutionIbans);
db.getCollection('maintenance').insertMany(maintenance);
db.getCollection('pspLegacy').insertMany(pspLegacy);
db.getCollection('tavoloOp').insertMany(tavoloOp);
db.getCollection('taxonomies').insertMany(taxonomies);
db.getCollection('taxonomy_groups').insertMany(taxonomy_groups);
db.getCollection('wrappers').insertMany(wrappers);

console.log("Initialization end");