// Public JS API for @babashka/fs, compiled from src/babashka/fs.cljc by squint.
// Every function is exported under a friendly camelCase name; option maps accept
// camelCase (or dashed) keys. The munged names (copy_tree, exists_QMARK_) are
// re-exported as-is for squint interop.
// Generated from script/index.template.mjs by script/gen_index.cljs (bb gen-index).
import * as raw from './lib/babashka/fs.mjs';
export * from './lib/babashka/fs.mjs';

// camelCase (or dashed) option keys are translated to the dashed keyword
// strings the squint-compiled functions read, e.g. {posixFilePermissions}
// and {visitFile} become {'posix-file-permissions'} / {'visit-file'}.
const kebabKey = (k) => k.replace(/[A-Z]/g, (m) => '-' + m.toLowerCase());
const isPlainObject = (x) =>
  x != null && typeof x === 'object' && Object.getPrototypeOf(x) === Object.prototype;
const kebabizeArg = (x) => {
  if (!isPlainObject(x)) return x;
  const out = {};
  for (const k of Object.keys(x)) out[kebabKey(k)] = x[k];
  return out;
};
const jsFriendly = (fn) => (...args) => fn(...args.map(kebabizeArg));

%CONSTS%

export {
%EXPORTS%
};

// with-temp-dir is a compile-time macro; this is the JS-callable equivalent.
// Creates a temp dir, runs `callback(dir)`, then deletes it (unless `opts.keep`).
export function withTempDir(callback, opts) {
  const o = kebabizeArg(opts);
  const dir = raw.create_temp_dir(o);
  try {
    return callback(dir);
  } finally {
    if (!(o && o.keep)) {
      raw.delete_tree(dir, { force: true });
    }
  }
}
