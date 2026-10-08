import fs from 'node:fs';
import path from 'node:path';

const reportPath = process.argv[2];

if (!reportPath) {
  console.error('Usage: node scripts/test-summary.mjs <test-report.json>');
  process.exit(1);
}

const report = JSON.parse(fs.readFileSync(reportPath, 'utf8'));
const featureLabels = new Map([
  ['app', 'Application'],
  ['auth', 'Auth'],
  ['dashboard', 'Dashboard'],
  ['inventory', 'Inventory'],
  ['products', 'Products'],
  ['reports', 'Reports'],
  ['users', 'Users'],
  ['core', 'Core'],
]);

const normalizeFeature = (value) => {
  const normalized = value.replaceAll('\\', '/');
  const match = normalized.match(/src\/app\/features\/([^/]+)/) ??
    normalized.match(/src\/app\/core\/([^/]+)/) ??
    normalized.match(/src\/app\/([^/]+)/);
  return match ? match[1] : 'Application';
};

const featureCoverage = new Map();
const featureTests = new Map();

for (const test of report.testResults ?? []) {
  const filePath = test.name ?? '';
  const feature = featureLabels.get(normalizeFeature(filePath)) ?? 'Application';

  const group = featureTests.get(feature) ?? {
    files: new Set(),
    passed: 0,
    failed: 0,
    pending: 0,
    duration: 0,
  };

  group.files.add(filePath);
  group.duration += (test.assertionResults ?? []).reduce(
    (total, result) => total + Number(result.duration ?? 0),
    0,
  );

  for (const result of test.assertionResults ?? []) {
    if (result.status === 'passed') group.passed += 1;
    else if (result.status === 'failed') group.failed += 1;
    else if (result.status === 'pending' || result.status === 'todo') group.pending += 1;
  }

  featureTests.set(feature, group);
}

const allCoverageFiles = Object.values(report.coverageMap ?? {});
for (const file of allCoverageFiles) {
  const sourcePath = file.path ?? '';
  const feature = featureLabels.get(normalizeFeature(sourcePath)) ??
    featureLabels.get(path.dirname(sourcePath).replaceAll('\\', '/')) ??
    'Application';

  const group = featureCoverage.get(feature) ?? {
    statements: { covered: 0, total: 0 },
    branches: { covered: 0, total: 0 },
    functions: { covered: 0, total: 0 },
    lines: { covered: 0, total: 0 },
  };

  const addCoverage = (metric, covered, total) => {
    group[metric].covered += covered;
    group[metric].total += total;
  };

  const statementEntries = Object.values(file.s ?? {});
  addCoverage('statements', statementEntries.filter((value) => Number(value) > 0).length, statementEntries.length);

  const functionEntries = Object.values(file.f ?? {});
  addCoverage('functions', functionEntries.filter((value) => Number(value) > 0).length, functionEntries.length);

  const branchEntries = Object.values(file.b ?? {});
  const coveredBranches = branchEntries.filter((value) => Array.isArray(value) && value.some((branch) => Number(branch) > 0)).length;
  addCoverage('branches', coveredBranches, branchEntries.length);

  const lineEntries = Object.values(file.statementMap ?? {});
  const coveredLines = statementEntries.filter((value) => Number(value) > 0).length;
  addCoverage('lines', coveredLines, lineEntries.length);

  featureCoverage.set(feature, group);
}

const formatPercent = (numerator, denominator) => {
  if (denominator === 0) return '—';
  return `${((numerator / denominator) * 100).toFixed(1)}%`;
};

const featureNames = [...new Set([...featureTests.keys(), ...featureCoverage.keys()])].sort((a, b) => a.localeCompare(b));
const rows = featureNames.map((feature) => {
  const tests = featureTests.get(feature) ?? {
    files: new Set(),
    passed: 0,
    failed: 0,
    pending: 0,
    duration: 0,
  };
  const coverage = featureCoverage.get(feature) ?? {
    statements: { covered: 0, total: 0 },
    branches: { covered: 0, total: 0 },
    functions: { covered: 0, total: 0 },
    lines: { covered: 0, total: 0 },
  };

  return [
    feature,
    tests.files.size,
    tests.passed,
    tests.failed,
    `${(tests.duration / 1000).toFixed(1)}s`,
    formatPercent(coverage.statements.covered, coverage.statements.total),
    formatPercent(coverage.branches.covered, coverage.branches.total),
    formatPercent(coverage.functions.covered, coverage.functions.total),
    formatPercent(coverage.lines.covered, coverage.lines.total),
  ];
});

const testFiles = [...featureTests.values()].reduce((total, group) => total + group.files.size, 0);
const totalPassed = report.numPassedTests ?? [...featureTests.values()].reduce((total, group) => total + group.passed, 0);
const totalFailed = report.numFailedTests ?? [...featureTests.values()].reduce((total, group) => total + group.failed, 0);
const totalDuration = ((report.testResults ?? []).reduce(
  (total, result) => total + (Number(result.endTime ?? 0) - Number(result.startTime ?? 0)),
  0,
) / 1000).toFixed(1);
const summary = `### ✅ Frontend Test Summary

| Feature | Test Files | Passed | Failed | Duration | Statements | Branches | Functions | Lines |
|---|---:|---:|---:|---:|---:|---:|---:|---:|
${rows.map((row) => `| ${row.join(' | ')} |`).join('\n')}

**Overall:** ${testFiles} test files · ${totalPassed} passed · ${totalFailed} failed · ${totalDuration}s`;

console.log(summary);

const summaryPath = process.env.GITHUB_STEP_SUMMARY;
if (summaryPath) fs.appendFileSync(summaryPath, `${summary}\n`);
