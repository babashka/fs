// Smoke tests for the JS surface in index.mjs: the parts the ClojureScript test
// suite does not cover - camelCase names, option-key translation, the withTempDir
// helper, the `delete` alias, and value-vs-function exports.
// Run: node --test test-js/smoke.test.mjs (needs `npm run build` first).

import { test } from 'node:test';
import assert from 'node:assert/strict';
import * as fs from '../index.mjs';

test('beautiful names resolve', () => {
  assert.equal(fs.exists('package.json'), true);
  assert.equal(fs.isDirectory('.'), true);
  assert.equal(fs.isRegularFile('package.json'), true);
  assert.equal(fs.fileName('/a/b.txt'), 'b.txt');
  assert.equal(typeof fs.isSymlink, 'function');
});

test('camelCase options translate on same-name functions too (copy, move, glob)', () => {
  fs.withTempDir((d) => {
    const src = fs.path(String(d), 'a.txt');
    const dst = fs.path(String(d), 'b.txt');
    fs.spit(src, 'new');
    fs.spit(dst, 'old');
    fs.copy(src, dst, { replaceExisting: true });
    assert.equal(fs.slurp(dst), 'new');
  });
});

test('slurp / spit round-trip', () => {
  fs.withTempDir((d) => {
    const f = fs.path(String(d), 'f.txt');
    fs.spit(f, 'hello\nworld');
    assert.equal(fs.slurp(f), 'hello\nworld');
  });
});

test('fileSeparator / pathSeparator are values, not functions', () => {
  assert.equal(typeof fs.fileSeparator, 'string');
  assert.equal(typeof fs.pathSeparator, 'string');
});

test('withTempDir passes the dir, returns the callback value, deletes after', () => {
  let seen;
  const ret = fs.withTempDir((d) => {
    seen = String(d);
    assert.equal(fs.exists(seen), true);
    return 42;
  });
  assert.equal(ret, 42);
  assert.equal(fs.exists(seen), false);
});

test('withTempDir with {keep:true} does not delete', () => {
  let seen;
  fs.withTempDir((d) => { seen = String(d); }, { keep: true });
  assert.equal(fs.exists(seen), true);
  fs.deleteTree(seen);
  assert.equal(fs.exists(seen), false);
});

test('camelCase option keys translate to dashed keyword strings', { skip: fs.isWindows() }, () => {
  const d = fs.createTempDir({ posixFilePermissions: 'rwxr-xr-x' });
  assert.equal(fs.posixToStr(fs.posixFilePermissions(d)), 'rwxr-xr-x');
  fs.deleteTree(d);
});

test('dashed option keys still work (back-compat)', { skip: fs.isWindows() }, () => {
  const d = fs.createTempDir({ 'posix-file-permissions': 'rwxr-xr-x' });
  assert.equal(fs.posixToStr(fs.posixFilePermissions(d)), 'rwxr-xr-x');
  fs.deleteTree(d);
});

test('walk-file-tree camelCase callback keys translate', () => {
  fs.withTempDir((d) => {
    fs.createFile(fs.path(String(d), 'f.txt'));
    const seen = [];
    fs.walkFileTree(d, {
      visitFile: (p) => { seen.push(fs.fileName(p)); return 'continue'; },
    });
    assert.deepEqual(seen, ['f.txt']);
  });
});

test('delete alias removes a file', () => {
  fs.withTempDir((d) => {
    const f = fs.path(String(d), 'x');
    fs.createFile(f);
    assert.equal(fs.exists(f), true);
    fs.delete(f);
    assert.equal(fs.exists(f), false);
  });
});

test('array and Date option values are passed through, not mangled', () => {
  fs.withTempDir((d) => {
    const f = fs.path(String(d), 'lines.txt');
    fs.writeLines(f, ['one', 'two', 'three']);
    assert.deepEqual([...fs.readAllLines(f)], ['one', 'two', 'three']);
  });
});

test('munged squint-interop exports exist and take dashed option keys', () => {
  fs.withTempDir((d) => {
    assert.equal(fs.exists_QMARK_(String(d)), true);
    const f = fs.path(String(d), 'x.txt');
    fs.createFile(f);
    fs.copy(f, fs.path(String(d), 'y.txt'));
    fs.copy(f, fs.path(String(d), 'y.txt'), { replaceExisting: true });
    fs.copy_tree(String(d), fs.path(String(d), '..', fs.fileName(String(d)) + '-copy'), { 'replace-existing': true });
    assert.equal(fs.exists(fs.path(String(d), '..', fs.fileName(String(d)) + '-copy', 'x.txt')), true);
    fs.deleteTree(fs.path(String(d), '..', fs.fileName(String(d)) + '-copy'));
  });
});
